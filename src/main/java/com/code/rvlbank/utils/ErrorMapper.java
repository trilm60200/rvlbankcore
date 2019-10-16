package com.code.rvlbank.utils;

import com.code.rvlbank.models.Account;
import com.code.rvlbank.models.Error;

public class ErrorMapper {

    /**
     * Mapping Error Msg
     * @param error
     * @return
     */
    public static Error errorMapping(String error) {
        return new Error(error);
    }

    /**
     * Converting error msg to Account obj
     * @param error
     * @return
     */
    public static Account convertError(String error) {
        Account account = new Account();
        account.setError(new Error(error));
        return account;
    }
}
