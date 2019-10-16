# rvlbankcore
Simple standalone application with HTTP REST API with Banking Logic
### 1. How to start? ###
Run 
mvn clean install
=> to compile application and run tests
 
Run 
mvn exec:java -Dexec.mainClass=RVLBankMain 
=> to launch application. It will create H2 file database ("db_rvlbank.mv.db") in the project directory and 
will start server on "8585" port.  


### 2. API ###
All calls to API must be started with `http://localhost:8080/api`
Please check API details in /documents/TDS - RVLBANKCORE.doc

### 3. Library ###
[Jersey](https://jersey.github.io) - AX-RS API - implement RESTful Web Services
[H2 Database Engine](http://www.h2database.com) - Java SQL file and in-memory databas
[jOOQ](https://www.jooq.org) -  typesafe SQL query construction and execution
[Flyway](https://flywaydb.org) - database migration tool
[Guice](https://github.com/google/guice) - lightweight dependency framework
[Lombok](https://projectlombok.org) - automatically plugs into your editor and build tools
[Spock](http://spockframework.org) - testing and specification framework

### 4. Missing ###
Implement reconciliation parser and logic.
                                                       