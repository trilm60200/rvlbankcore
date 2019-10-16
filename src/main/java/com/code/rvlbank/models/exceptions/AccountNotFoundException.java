package com.code.rvlbank.models.exceptions;

public class AccountNotFoundException extends RuntimeException {
    private String accountId;

    public AccountNotFoundException(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountId() {
        return accountId;
    }

    @Override
    public String getMessage() {
        return "Can't find account with accountRef=" + accountId;
    }
}
