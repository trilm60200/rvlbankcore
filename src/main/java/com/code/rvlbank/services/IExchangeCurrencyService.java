package com.code.rvlbank.services;

import com.code.rvlbank.models.Fund;

import java.math.BigDecimal;
import java.util.List;

public interface IExchangeCurrencyService {
    Fund exchange(Fund source, String targetCurrency);
    List<String> getSupportedCurrencies();
    boolean isCurrencySupported(String currency);
    BigDecimal exchangeRate(String sourceCurrency,
                            String targetCurrency);
}
