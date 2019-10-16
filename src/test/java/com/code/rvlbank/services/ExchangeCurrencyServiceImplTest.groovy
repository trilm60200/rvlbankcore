package com.code.rvlbank.services

import com.code.rvlbank.injection.ServiceInjectorModule
import com.code.rvlbank.models.Fund
import com.code.rvlbank.services.IExchangeCurrencyService
import com.code.rvlbank.services.impl.ExchangeCurrencyServiceImpl
import com.google.inject.Injector
import spock.lang.Shared
import spock.lang.Specification

class ExchangeCurrencyServiceImplTest extends Specification {

    @Shared
    def converter = new ExchangeCurrencyServiceImpl()

    def "exchange rate for existing currencies"() {
        expect:
        converter.exchangeRate("EUR", "CNY") == 7.7943
    }

    def "exchange rate for nonexistent currency"() {
        when:
        converter.exchangeRate("EUR", "XXX")
        then:
        IllegalArgumentException ex = thrown()
        ex.message == "Can't find exchange rate for XXX"
    }

    def "exchange money in existing currencies"() {
        when:
        def money = converter.exchange(
                new Fund(new BigDecimal("100"), "EUR"), "CNY")
        then:
        money.currency == "CNY"
        money.amount != 100
    }

    def "exchange money in nonexistent currencies"() {
        when:
        converter.exchange(
                new Fund(new BigDecimal("10"), "EUR"), "YYY")
        then:
        IllegalArgumentException ex = thrown()
        ex.message == "Can't find exchange rate for YYY"
    }

    def "exchange money to same currency"() {
        when:
        def money = converter.exchange(
                new Fund(new BigDecimal("5"), "EUR"), "EUR")
        then:
        money.currency == "EUR"
        money.amount == 5
    }


    def "check for currency support"() {
        expect:
        converter.isCurrencySupported("EUR")
        !converter.isCurrencySupported("YYY")
    }
}
