package com.code.rvlbank.services.impl;

import com.code.rvlbank.entities.jooq.tables.records.AccountRecord;
import com.code.rvlbank.models.Account;
import com.code.rvlbank.models.Error;
import com.code.rvlbank.models.Fund;
import com.code.rvlbank.models.exceptions.AccountNotFoundException;
import com.code.rvlbank.services.IAccountService;
import com.code.rvlbank.services.IDatabaseManager;
import com.code.rvlbank.services.IExchangeCurrencyService;
import com.code.rvlbank.utils.ErrorMapper;
import com.google.inject.Singleton;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.code.rvlbank.entities.jooq.Tables.ACCOUNT;

import javax.inject.Inject;
import java.util.Objects;

@Singleton
public class AccountServiceImpl implements IAccountService {
    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    private IDatabaseManager databaseManager;
    private IExchangeCurrencyService exchangeCurrencyService;

    @Inject
    public void setDatabaseManager(IDatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Inject
    public void setExchangeService(IExchangeCurrencyService exchangeCurrencyService) {
        this.exchangeCurrencyService = exchangeCurrencyService;
    }

    @Override
    public Account createAccount(String accountRef) {
        Account account = getAccount(accountRef);
        if (Objects.nonNull(account) && account.getId() != null) {
            return ErrorMapper.convertError("This account was already bound");
        }

        AccountRecord record = databaseManager.getSqlDSL()
                .insertInto(ACCOUNT, ACCOUNT.ACCOUNT_REF)
                .values(accountRef)
                .returning().fetchOne();
        return convertFrom(record);
    }

    @Override
    public Account createAccount(String accountRef, String currency) {
        if (!exchangeCurrencyService.isCurrencySupported(currency)) {
            return ErrorMapper.convertError("This currency is not supported.");
        }

        AccountRecord record = databaseManager.getSqlDSL()
                .insertInto(ACCOUNT, ACCOUNT.ACCOUNT_REF, ACCOUNT.CURRENCY)
                .values(accountRef, currency)
                .returning().fetchOne();
        return convertFrom(record);
    }

    @Override
    public Account getAccount(String accountRef) {
        AccountRecord accountRecord = databaseManager.getSqlDSL()
                .selectFrom(ACCOUNT)
                .where(ACCOUNT.ACCOUNT_REF.eq(accountRef).and(ACCOUNT.LOCKED.isFalse().and(ACCOUNT.IS_ACTIVE.isTrue())))
                .fetchOne();
        if (accountRecord != null) {
            return convertFrom(accountRecord);
        }
        return ErrorMapper.convertError("Account not exist OR locked.");
    }

    @Override
    public boolean isLocked(String accountRef) {
        Record1<Boolean> locked = databaseManager.getSqlDSL()
                .select(ACCOUNT.LOCKED).from(ACCOUNT)
                .where(ACCOUNT.ACCOUNT_REF.eq(accountRef))
                .fetchOne();
        if (locked == null) {
            throw new AccountNotFoundException(accountRef);
        }
        return locked.get(ACCOUNT.LOCKED);
    }

    @Override
    public void unbindAccount(String accountRef) {
        boolean accountExists = databaseManager.getSqlDSL()
                .deleteFrom(ACCOUNT)
                .where(ACCOUNT.ACCOUNT_REF.eq(accountRef))
                .execute() > 0;
        if (!accountExists) {
            throw new AccountNotFoundException("Not found account: " + accountRef);
        }
    }

    @Override
    public void lockAccount(String accountRef) {
        DSLContext sql = databaseManager.getSqlDSL();
        boolean accountExists = sql.transactionResult(configuration ->
                DSL.using(configuration)
                        .update(ACCOUNT)
                        .set(ACCOUNT.LOCKED, true)
                        .where(ACCOUNT.ACCOUNT_REF.eq(accountRef))
                        .execute() > 0);
        if (!accountExists) {
            throw new AccountNotFoundException("Not found account: " + accountRef);
        }
    }

    @Override
    public void unlockAccount(String accountRef) {
        DSLContext sql = databaseManager.getSqlDSL();
        boolean accountExists = sql.transactionResult(configuration ->
                DSL.using(configuration)
                        .update(ACCOUNT)
                        .set(ACCOUNT.LOCKED, false)
                        .where(ACCOUNT.ACCOUNT_REF.eq(accountRef))
                        .execute() > 0);
        if (!accountExists) {
            throw new AccountNotFoundException("Not found account: " + accountRef);
        }
    }

    private Account convertFrom(AccountRecord accountRecord) {
        Account account = new Account();
        account.setId(accountRecord.getId());
        account.setAccountRef(accountRecord.getAccountRef());
        account.setLocked(accountRecord.getLocked());

        Fund balance = new Fund();
        balance.setAmount(accountRecord.getBalance());
        balance.setCurrency(accountRecord.getCurrency());
        account.setBalance(balance);
        account.setActive(accountRecord.getIsActive());
        return account;
    }




}
