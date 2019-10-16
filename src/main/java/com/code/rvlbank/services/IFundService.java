package com.code.rvlbank.services;

import com.code.rvlbank.models.Fund;

public interface IFundService {
    Fund getBalance(String accountRef);

    void transfer(String fromId, String toId, Fund moneyToTransfer);

    void withdraw(String accountRef, Fund moneyToWithdraw);

    void deposit(String accountRef, Fund moneyToDeposit);
}
