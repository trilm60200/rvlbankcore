package com.code.rvlbank.models.exceptions;

public class AccountLockedException extends RuntimeException {
    private String accountRef;

    public AccountLockedException(String accountRef) {
        this.accountRef = accountRef;
    }

    public String getAccountId() {
        return accountRef;
    }

    @Override
    public String getMessage() {
        return "Account with ID=" + accountRef + " is locked";
    }
}
