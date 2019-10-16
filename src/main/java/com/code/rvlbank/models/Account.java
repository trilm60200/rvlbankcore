package com.code.rvlbank.models;

import lombok.Data;

@Data
public class Account {
    private Long id;
    private String accountRef;
    private boolean locked;
    private Fund balance;
    private boolean isActive;

    private Error error;

}
