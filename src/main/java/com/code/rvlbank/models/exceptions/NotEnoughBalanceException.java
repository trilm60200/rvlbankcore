package com.code.rvlbank.models.exceptions;

public class NotEnoughBalanceException extends RuntimeException {
    private String accountId;
    private String operation;

    public NotEnoughBalanceException(String accountId, String operation) {
        this.accountId = accountId;
        this.operation = operation;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getOperation() {
        return operation;
    }

    @Override
    public String getMessage() {
        return "Account with ID="+accountId+" doesn't have enough money to complete " + operation;
    }
}
