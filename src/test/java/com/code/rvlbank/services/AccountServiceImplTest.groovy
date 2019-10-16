package com.code.rvlbank.services

import com.code.rvlbank.configuration.DatabaseConfiguration
import com.code.rvlbank.models.Account
import com.code.rvlbank.models.Fund
import com.code.rvlbank.models.exceptions.AccountNotFoundException
import com.code.rvlbank.services.impl.AccountServiceImpl
import com.code.rvlbank.services.impl.DatabaseManagerImpl
import com.code.rvlbank.services.impl.ExchangeCurrencyServiceImpl
import spock.lang.Shared
import spock.lang.Specification

class AccountServiceImplTest extends Specification {

    @Shared
    def accountService = new AccountServiceImpl()

    // run before the first feature method
    def setupSpec() {
        DatabaseConfiguration.dbconfig()
        accountService.setDatabaseManager(new DatabaseManagerImpl())
        accountService.setExchangeService(new ExchangeCurrencyServiceImpl())
    }

    def "bind account"() {
        when:
        def account = accountService.createAccount("1234123412341234")
        then:
        account.id != 0
        account.accountRef != ""
        !account.locked
    }

    def "bind existing account"() {
        when:
        def account = accountService.createAccount("1234123412341234")
        then:
        account.error != null
    }

    def "bind account with currency"() {
        when:
        def account = accountService.createAccount("123412341234444", "CNY")
        then:
        account.id != 0
        account.accountRef != ""
        !account.locked
        account.balance.amount == 0
        account.balance.currency == "CNY"
    }

    def "query account"() {
        setup:
        def expectedAccount = new Account()
        expectedAccount.with {
            id = 3
            accountRef = 1234432112344321
            locked = false
            balance = new Fund()
            balance.with {
                amount = new BigDecimal("10000.0000")
                currency = "EUR"
            }
            isActive = true
        }
        expect:
        expectedAccount == accountService.getAccount("1234432112344321")
    }

    def "bind, query and unbind existing account"() {
        def accountCreated = accountService.createAccount("11113333444455556")
        def accountRef = accountCreated.accountRef
        expect:
        accountCreated == accountService.getAccount(accountRef)
        accountService.unbindAccount(accountRef)
        when:
        accountCreated = accountService.getAccount(accountRef)
        then:
        accountCreated.id == null
    }

    def "bind, reject, approve and query account"() {
        def account = accountService.createAccount("9999888877778888")
        def accountRef = account.accountRef
        expect:
        !account.locked
        when:
        accountService.lockAccount(accountRef)
        account = accountService.getAccount(accountRef)
        then:
        AccountNotFoundException ex = thrown()
        ex.message != null
        when:
        accountService.unlockAccount("9999888877778888")
        account = accountService.getAccount("9999888877778888")
        then:
        account.id != null
    }

    def "unbind nonexistent account"() {
        when:
        accountService.unbindAccount("99998888777788889")
        then:
        AccountNotFoundException ex = thrown()
        ex.message != null
    }

    def "reject nonexistent account"() {
        when:
        accountService.lockAccount("99998888777788889")
        then:
        AccountNotFoundException ex = thrown()
        ex.message != null
    }

}
