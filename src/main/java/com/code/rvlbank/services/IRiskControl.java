package com.code.rvlbank.services;

public interface IRiskControl {
    void createLock(String accountId);
    void removeLock(String accountId);
    void doInLock(String accountId, Runnable action);
    void doInLock(String accountId1, String accountId2, Runnable action);
}
