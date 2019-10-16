package com.code.rvlbank.services

import com.code.rvlbank.services.impl.RiskControlImpl
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Timeout

import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

class RiskControlImplTest extends Specification {

    def lockManager = new RiskControlImpl()

    @Shared
    def taskExecutor = Executors.newFixedThreadPool(4)

    def "implicit lock creation"() {
        setup:
        def accountRef = "1111333344445555"
        when:
        lockManager.doInLock(accountRef) { println "locked account 1111333344445555 !!! " }
        then:
        noExceptionThrown()
        lockManager.locks.containsKey(accountRef)
    }

    def "create/remove lock"() {
        setup:
        def accountRef = "1111333344445555"
        lockManager.createLock(accountRef)
        when:
        lockManager.removeLock(accountRef)
        then:
        !lockManager.locks.containsKey(accountRef)
        when:
        lockManager.createLock(accountRef)
        then:
        lockManager.locks.containsKey(accountRef)
    }

    def "remove nonexistent lock"() {
        when:
        lockManager.removeLock("not_exist")
        then:
        noExceptionThrown()
    }

    @Timeout(30)
    def "exception doesn't block account"() {
        setup:
        def accountRef = "1111333344445555"
        lockManager.createLock(accountRef)
        when:
        taskExecutor.submit { lockManager.doInLock(accountRef) { throw new RuntimeException("Some exception") } }.get()
        then:
        def ex = thrown(ExecutionException.class)
        ex.getCause().class == RuntimeException.class
        when:
        taskExecutor.submit { lockManager.doInLock(accountRef) { println "just success action" } }.get()
        then:
        noExceptionThrown()
    }

    @Timeout(30)
    def "doInLock each accounts => not block others"() {
        setup:
        def accountRef1 = "1111333344445555"
        def accountRef2 = "9999888877778888"
        lockManager.createLock(accountRef1)
        lockManager.createLock(accountRef2)
        def AtoBCounter = 0, BtoACounter = 0, tasks = []
        when:
        1000.times {
            tasks << { lockManager.doInLock(accountRef1, accountRef2) { AtoBCounter++ } }
            tasks << { lockManager.doInLock(accountRef2, accountRef1) { BtoACounter++ } }
        }
        taskExecutor.invokeAll(tasks).forEach({ it.get() })
        then:
        AtoBCounter == 1000
        BtoACounter == 1000
    }

    @Timeout(30)
    def "doInLock each/both accounts => not block others"() {
        setup:
        def accountRef1 = "1111333344445555"
        def accountRef2 = "9999888877778888"
        lockManager.createLock(accountRef1)
        lockManager.createLock(accountRef2)
        def ACounter = 0, BCounter = 0, AtoBCounter = 0, tasks = []
        when:
        1000.times {
            tasks << { lockManager.doInLock(accountRef1) { ACounter++ } }
            tasks << { lockManager.doInLock(accountRef1, accountRef2) { AtoBCounter++ } }
            tasks << { lockManager.doInLock(accountRef1) { BCounter++ } }
        }
        taskExecutor.invokeAll(tasks).forEach({ it.get() })
        then:
        AtoBCounter == 1000
        ACounter == 1000
        BCounter == 1000
    }

    //stop condition for 'waiting'
    @Timeout(30)
    def "doInLock each/both accounts => not block others when have exceptions"() {
        setup:
        def accountRef1 = "1111333344445555"
        def accountRef2 = "9999888877778888"
        lockManager.createLock(accountRef1)
        lockManager.createLock(accountRef2)
        def ACounter = 0, BCounter = 0, AtoBCounter = 0, tasks = []
        when:
        1000.times { i ->
            tasks << {
                lockManager.doInLock(accountRef1) {
                    if (i % 2 == 0) ACounter++
                    else throw new RuntimeException("lock in A, exception")
                }
            }
            tasks << { lockManager.doInLock(accountRef1, accountRef2) { AtoBCounter++ } }
            tasks << {
                lockManager.doInLock(accountRef2) {
                    if (i % 2 == 1) BCounter++
                    else throw new RuntimeException("lock in B, exception")
                }
            }
        }
        taskExecutor.invokeAll(tasks).forEach({
            try {
                it.get()
            } catch (Exception ignore) {}
        })
        then:
        AtoBCounter == 1000
        ACounter == 500 //even numbers from 0 to 999
        BCounter == 500 //odd numbers from 0 to 999
    }

    @Timeout(30)
    def "check concurrency for doInLock with two accounts"() {
        setup:
        def accountRef1 = "1111333344445555"
        def accountRef2 = "9999888877778888"
        def accountRef3 = "123412341234444"
        lockManager.createLock(accountRef1)
        lockManager.createLock(accountRef2)
        lockManager.createLock(accountRef3)
        def counter = 10, tasks = []
        when:
        1000.times {
            tasks << { lockManager.doInLock(accountRef1, accountRef2) { counter++ } }
            tasks << { lockManager.doInLock(accountRef2, accountRef3) { counter-- } }
        }
        taskExecutor.invokeAll(tasks).forEach({ it.get() })
        then:
        counter == 10
    }


    def cleanupSpec() {
        taskExecutor.shutdownNow()
    }
}
