package com.code.rvlbank.services.impl;

import com.code.rvlbank.services.IDatabaseManager;
import com.google.inject.Singleton;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Slf4j
@Singleton
public class DatabaseManagerImpl implements IDatabaseManager {
    private final DSLContext dslContext;
    private DataSource dataSource;

    private static final Logger log = LoggerFactory.getLogger(DatabaseManagerImpl.class);

    public DatabaseManagerImpl() {
        try {
            Properties properties = new Properties();
            properties.load(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("db/db.properties"));
            dslContext = createDSLContext(properties);
        } catch (IOException e) {
            throw new RuntimeException("Error while loading DB connection properties", e);
        }
    }

    public DatabaseManagerImpl(Properties properties) {
        dslContext = createDSLContext(properties);
    }

    private DSLContext createDSLContext(Properties dbProps) {
        log.debug("DB properties: {}", dbProps);
        try {
            ComboPooledDataSource pooledDataSource = new ComboPooledDataSource();
            pooledDataSource.setDriverClass(dbProps.getProperty("jdbc.driver"));
            pooledDataSource.setUser( dbProps.getProperty("jdbc.username"));
            pooledDataSource.setPassword(dbProps.getProperty("jdbc.password"));
            pooledDataSource.setJdbcUrl(dbProps.getProperty("jdbc.url"));
            dataSource = pooledDataSource;
            return DSL.using(dataSource, SQLDialect.H2);
        } catch (Exception e) {
            throw new RuntimeException("Error while connecting to database", e);
        }
    }

    @Override
    public DSLContext getSqlDSL() {
        return dslContext;
    }

    @Override
    public void close() {
        try {
            DataSources.destroy(dataSource);
            dslContext.close();
        } catch (Exception e) {
            log.warn("Error while closing DatabaseManager", e);
        }
    }
}

