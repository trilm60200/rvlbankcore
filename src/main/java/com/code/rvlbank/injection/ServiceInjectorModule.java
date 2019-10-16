package com.code.rvlbank.injection;

import com.code.rvlbank.services.*;
import com.code.rvlbank.services.impl.*;
import com.google.inject.AbstractModule;

public class ServiceInjectorModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(IDatabaseManager.class).to(DatabaseManagerImpl.class);
        bind(IRiskControl.class).to(RiskControlImpl.class);
        bind(IExchangeCurrencyService.class).to(ExchangeCurrencyServiceImpl.class);
        bind(IAccountService.class).to(AccountServiceImpl.class);
        bind(IFundService.class).to(FundServiceImpl.class);

    }
}
