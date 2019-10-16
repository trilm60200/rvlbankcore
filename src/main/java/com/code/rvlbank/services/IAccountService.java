package com.code.rvlbank.services;

import com.code.rvlbank.models.Account;

public interface IAccountService {
    Account createAccount(String accountRef);
    Account createAccount(String accountRef, String currency);
    Account getAccount(String accountRef);
    boolean isLocked(String accountRef);
    void unbindAccount(String accountRef);

    // Lock account in case realize any strange requests.
    void lockAccount(String accountRef);
    void unlockAccount(String accountRef);
}
