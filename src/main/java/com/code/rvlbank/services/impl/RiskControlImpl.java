package com.code.rvlbank.services.impl;

import com.code.rvlbank.models.exceptions.InvalidRequestException;
import com.code.rvlbank.services.IRiskControl;
import com.google.inject.Singleton;
import org.jooq.tools.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Singleton
public class RiskControlImpl implements IRiskControl {

    Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    @Override
    public void createLock(String accountId) {
        locks.putIfAbsent(accountId, new ReentrantLock());
    }
    @Override
    public void removeLock(String accountRef) {
        locks.remove(accountRef);
    }

    @Override
    public void doInLock(String accountRef, Runnable action) {
        if (StringUtils.isBlank(accountRef))
            throw new InvalidRequestException("Invalid or Missing request field");

        createLock(accountRef);
        ReentrantLock lock = locks.get(accountRef);
        lock.lock();
        try {
            action.run();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void doInLock(String accountRef1, String accountRef2, Runnable action) {
        if (StringUtils.isBlank(accountRef1) || StringUtils.isBlank(accountRef2))
            throw new InvalidRequestException("Invalid or Missing request field");

        createLock(accountRef1);
        createLock(accountRef2);
        ReentrantLock lock1 = locks.get(accountRef1);
        ReentrantLock lock2 = locks.get(accountRef2);
        boolean gotTwoLocks = false;
        do {
            if (lock1.tryLock()) {
                if (lock2.tryLock()) {
                    gotTwoLocks = true;
                } else {
                    lock1.unlock();
                }
            }
        } while (!gotTwoLocks);
        try {
            action.run();
        } finally {
            lock2.unlock();
            lock1.unlock();
        }
    }
}
