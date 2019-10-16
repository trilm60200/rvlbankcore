package com.code.rvlbank.models.exceptions;

public class FundNegativeAmountException extends RuntimeException {
    private String operation;

    public FundNegativeAmountException(String operation) {
        this.operation = operation;
    }

    @Override
    public String getMessage() {
        return "Can't " + operation + " negative amount of money";
    }
}
