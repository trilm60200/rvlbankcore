package com.code.rvlbank.services.impl;

import com.code.rvlbank.models.Fund;
import com.code.rvlbank.services.IExchangeCurrencyService;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

@Singleton
public class ExchangeCurrencyServiceImpl implements IExchangeCurrencyService {
    private Map<String, BigDecimal> rates = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(ExchangeCurrencyServiceImpl.class);

    public ExchangeCurrencyServiceImpl() {
        Properties properties = new Properties();
        try (InputStream ratesStream =
                     Thread.currentThread().getContextClassLoader().getResourceAsStream("exchange_rates.properties")) {
            properties.load(ratesStream);
            for (String currency : properties.stringPropertyNames()) {
                rates.put(currency, new BigDecimal(properties.getProperty(currency)));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while reading exchange rates", e);
        }
    }

    @Override
    public Fund exchange(Fund source, String targetCurrency) {
        BigDecimal rate = exchangeRate(source.getCurrency(), targetCurrency);
        BigDecimal newAmount = Fund.multiply(source.getAmount(), rate);
        return new Fund(newAmount, targetCurrency);
    }

    @Override
    public List<String> getSupportedCurrencies() {
        return new ArrayList<>(Collections.unmodifiableSet(rates.keySet()));
    }

    @Override
    public boolean isCurrencySupported(String currency) {
        return rates.containsKey(currency);
    }

    @Override
    public BigDecimal exchangeRate(String sourceCurrency, String targetCurrency) {
        BigDecimal sourceRate = rates.get(sourceCurrency);
        BigDecimal targetRate = rates.get(targetCurrency);
        if (sourceRate == null) {
            throw new IllegalArgumentException("Can't find exchange rate for " + sourceCurrency);
        }
        if (targetRate == null) {
            throw new IllegalArgumentException("Can't find exchange rate for " + targetCurrency);
        }
        return Fund.multiply(sourceRate, targetRate);
    }
}
