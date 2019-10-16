package com.code.rvlbank.services

import com.code.rvlbank.configuration.DatabaseConfiguration
import com.code.rvlbank.injection.InjectorProvider
import com.code.rvlbank.injection.ServiceInjectorModule
import com.code.rvlbank.models.Fund
import com.code.rvlbank.models.exceptions.AccountLockedException
import com.code.rvlbank.models.exceptions.AccountNotFoundException
import com.code.rvlbank.models.exceptions.FundNegativeAmountException
import com.code.rvlbank.models.exceptions.NotEnoughBalanceException
import com.code.rvlbank.services.IAccountService
import com.code.rvlbank.services.IFundService
import com.code.rvlbank.services.impl.FundServiceImpl
import com.google.inject.Guice
import com.google.inject.Injector
import spock.lang.Shared
import spock.lang.Specification

class FundServiceImplTest extends Specification {

    @Shared
    IFundService fundService = null

    @Shared
    IAccountService accountService = null


    def setupSpec() {
        DatabaseConfiguration.dbconfig()
        Injector injector = Guice.createInjector(new ServiceInjectorModule())
        fundService = injector.getInstance(IFundService.class)
        accountService = injector.getInstance(IAccountService.class)
    }

    // Get balance from mock data
    def "get balance of existing account"() {
        expect:
        new Fund(new BigDecimal("100.0000"), "EUR") == fundService.getBalance("1234123412341234")
    }

    def "get balance of non-existing account"() {
        def accountRef = "Not_exist"
        when:
        fundService.getBalance(accountRef)
        then:
        AccountNotFoundException e = thrown()
        e.message != null
    }

    def "deposit and withdraw in the same currency"() {
        def accountRef = "1234123412341234" //100 EUR
        when:
        fundService.deposit(accountRef, new Fund(new BigDecimal("100.0000"), "EUR"))
        then:
        fundService.getBalance(accountRef) == new Fund(new BigDecimal("200.0000"), "EUR")
        when:
        fundService.withdraw(accountRef, new Fund(new BigDecimal("100.0000"), "EUR"))
        then:
        fundService.getBalance(accountRef) == new Fund(new BigDecimal("100.0000"), "EUR")
    }

    def "deposit to non-existing account"() {
        def accountRef = "Not_exist"
        when:
        fundService.deposit(accountRef, new Fund(new BigDecimal("100"), "EUR"))
        then:
        AccountNotFoundException e = thrown()
        e.message != null
    }

    def "deposit negative money => account locked"() {
        def accountRef = "1234123412341234"
        when:
        fundService.deposit(accountRef, new Fund(new BigDecimal("-100"), "EUR"))
        then:
        NegativeArraySizeException e = thrown()
        e.message != null
        accountService.unlockAccount(accountRef)

    }

    def "deposit from locked account"() {
        def accountRef = "1234123412341234" //100 EUR
        when:
        //lock account
        accountService.lockAccount(accountRef)
        fundService.deposit(accountRef, new Fund(new BigDecimal("10"), "EUR"))
        then:
        AccountLockedException e = thrown()
        e.message != null
    }

    def "withdraw from non-existing account"() {
        def accountRef = "non_exist"
        when:
        fundService.withdraw(accountRef, new Fund(new BigDecimal("100"), "EUR"))
        then:
        AccountNotFoundException e = thrown()
        e.message != null
    }

    def "withdraw negative amount"() {
        def accountRef = "1234123412341234" //100 EUR
        when:
        fundService.withdraw(accountRef, new Fund(new BigDecimal("-100"), "EUR"))
        then:
        FundNegativeAmountException e = thrown()
        e.message != null
    }

    def "withdraw exceeding amount"() {
        def accountRef = "123412341234444" //1000 EUR
        when:
        fundService.withdraw(accountRef, new Fund(new BigDecimal("20000"), "EUR"))
        then:
        NotEnoughBalanceException e = thrown()
        e.message != null
    }

    def "withdraw from locked account"() {
        def accountRef = "1234123412341234" //100 EUR
        when:
        // lock account
        accountService.lockAccount(accountRef)
        fundService.withdraw(accountRef, new Fund(new BigDecimal("10"), "EUR"))
        then:
        AccountLockedException e = thrown()
        e.message != null
        //unlock account
        accountService.unbindAccount(accountRef)

    }

    def "transfer negative amount"() {
        def accountRef1 = "1111333344445555"
        def accountRef2 = "123412341234444"
        accountService.unlockAccount(accountRef1)
        accountService.unlockAccount(accountRef2)
        when:
        fundService.transfer(accountRef1, accountRef2, new Fund(new BigDecimal("-50"), "EUR"))
        then:
        FundNegativeAmountException e = thrown()
        e.message != null
    }

    def "transfer exceeding amount"() {
        def accountRef1 = "1111333344445555" //100 EUR
        def accountRef2 = "123412341234444" // 1000 EUR
        accountService.unlockAccount(accountRef1)
        accountService.unlockAccount(accountRef2)
        when:
        fundService.transfer(accountRef1, accountRef2, new Fund(new BigDecimal("30000"), "EUR"))
        then:
        NotEnoughBalanceException e = thrown()
        e.message != null
    }

    def "transfer to locked account"() {
        when:
        accountService.lockAccount("9999888877778888")
        fundService.transfer("9999888877778888", "123412341234444", new Fund(new BigDecimal("20"), "EUR"))
        then:
        AccountLockedException e = thrown()
        e.message != null
        accountService.unlockAccount("9999888877778888")
    }


}
