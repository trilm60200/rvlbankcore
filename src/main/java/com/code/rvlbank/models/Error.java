package com.code.rvlbank.models;

import java.io.Serializable;

public class Error implements Serializable {
    private String error;

    public Error() {
    }

    public Error(String error) {
        this.error = error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
