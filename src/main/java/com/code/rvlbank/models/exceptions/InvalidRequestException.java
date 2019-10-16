package com.code.rvlbank.models.exceptions;

public class InvalidRequestException extends RuntimeException {
    private String requestInfo;

    public InvalidRequestException(String requestInfo) {
        this.requestInfo = requestInfo;
    }

    public String getAccountId() {
        return requestInfo;
    }

    @Override
    public String getMessage() {
        return "Invalid Request Field: " + requestInfo;
    }
}
