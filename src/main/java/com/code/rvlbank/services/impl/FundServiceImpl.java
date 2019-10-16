package com.code.rvlbank.services.impl;

import com.code.rvlbank.entities.jooq.tables.Account;
import com.code.rvlbank.models.Error;
import com.code.rvlbank.models.Fund;
import com.code.rvlbank.models.exceptions.AccountLockedException;
import com.code.rvlbank.models.exceptions.AccountNotFoundException;
import com.code.rvlbank.models.exceptions.FundNegativeAmountException;
import com.code.rvlbank.models.exceptions.NotEnoughBalanceException;
import com.code.rvlbank.services.*;
import com.code.rvlbank.utils.ErrorMapper;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import static com.code.rvlbank.entities.jooq.Tables.ACCOUNT;


public class FundServiceImpl implements IFundService {
    private static final Logger log = LoggerFactory.getLogger(FundServiceImpl.class);

    private IDatabaseManager databaseManager;
    private IRiskControl riskControl;
    private IExchangeCurrencyService exchangeService;
    private IAccountService accountService;

    @Inject
    public void setDatabaseManager(IDatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Inject
    public void setRiskControl(IRiskControl riskControl) {
        this.riskControl = riskControl;
    }

    @Inject
    public void setExchangeService(IExchangeCurrencyService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @Inject
    public void setAccountService(IAccountService accountService) {
        this.accountService = accountService;
    }


    @Override
    public Fund getBalance(String accountRef) {
        Optional<Map<String, Object>> map = databaseManager.getSqlDSL()
                .select(Account.ACCOUNT.BALANCE, Account.ACCOUNT.CURRENCY)
                .from(Account.ACCOUNT)
                .where(Account.ACCOUNT.ACCOUNT_REF.eq(accountRef)).fetchOptionalMap();
        Fund fund = new Fund();
        if (map.isPresent()) {
            fund.setAmount((BigDecimal) map.get().get(Account.ACCOUNT.BALANCE.getName()));
            fund.setCurrency((String) map.get().get(Account.ACCOUNT.CURRENCY.getName()));
            return fund;
        } else {
            throw new AccountNotFoundException("Account not found");
        }
    }

    @Override
    public void transfer(String fromId, String toId, Fund moneyToTransfer) {
        if (moneyToTransfer.getAmount().signum() == -1) {
            // Lock account if invalid request
            accountService.lockAccount(fromId);
            throw new FundNegativeAmountException("transfer invalid request");
        }
        riskControl.doInLock(fromId, toId, () ->
                databaseManager.getSqlDSL().transaction(configuration -> {
                    checkForLock(fromId);
                    checkForLock(toId);
                    withdraw(fromId, moneyToTransfer);
                    deposit(toId, moneyToTransfer);
                })
        );
    }

    @Override
    public void withdraw(String accountRef, Fund fundWithdraw) {
        if (fundWithdraw.getAmount().signum() == -1) {
            // Lock account if invalid request
            accountService.lockAccount(accountRef);
            throw new FundNegativeAmountException("Input wrong amount.");
        }
        riskControl.doInLock(accountRef, () ->
                databaseManager.getSqlDSL().transaction(configuration -> {
                    checkForLock(accountRef);
                    Fund balance = getBalance(accountRef);
                    Fund withdrawInCurrency =
                            exchangeService.exchange(fundWithdraw, balance.getCurrency());
                    Fund newBalance = Fund.subtract(balance, withdrawInCurrency);
                    log.trace("withdraw: id={}, old={}, withdraw={}, withdrawInCurrency={}, new={}",
                            accountRef, balance, fundWithdraw, withdrawInCurrency, newBalance);
                    if (newBalance == null || newBalance.getAmount().signum() == -1) {
                        throw new NotEnoughBalanceException(accountRef, "Not enough balance");
                    }
                    DSL.using(configuration)
                            .update(ACCOUNT)
                            .set(ACCOUNT.BALANCE, newBalance.getAmount())
                            .set(ACCOUNT.CURRENCY, newBalance.getCurrency())
                            .where(ACCOUNT.ACCOUNT_REF.eq(accountRef)).execute();
                }));
    }


    @Override
    public void deposit(String accountRef, Fund fundDeposit) {
        if (fundDeposit.getAmount().signum() == -1) {
            // Lock account if invalid request
            accountService.lockAccount(accountRef);
            throw new NegativeArraySizeException("Input wrong amount.");
        }
        riskControl.doInLock(accountRef, () ->
                databaseManager.getSqlDSL().transaction(configuration -> {
                    checkForLock(accountRef);
                    Fund balance = getBalance(accountRef);
                    if (balance == null)
                        accountService.lockAccount(accountRef);

                    Fund depositInCurrency =
                            exchangeService.exchange(fundDeposit, balance.getCurrency());
                    Fund newBalance = Fund.add(balance, fundDeposit);
                    log.trace("deposit: id={}, old={}, deposit={}, depositInCurrency={}, new={}",
                            accountRef, balance, fundDeposit, depositInCurrency, newBalance);
                    DSL.using(configuration)
                            .update(ACCOUNT)
                            .set(ACCOUNT.BALANCE, newBalance.getAmount())
                            .set(ACCOUNT.CURRENCY, newBalance.getCurrency())
                            .where(ACCOUNT.ACCOUNT_REF.eq(accountRef)).execute();
                }));
    }

    private void checkForLock(String accountRef) {
        if (accountService.isLocked(accountRef)) {
            throw new AccountLockedException(accountRef);
        }
    }



}
