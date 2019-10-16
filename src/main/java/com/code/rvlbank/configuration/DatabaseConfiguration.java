package com.code.rvlbank.configuration;

import com.code.rvlbank.properties.JDBCProperties;
import org.flywaydb.core.Flyway;

import java.util.Properties;

public class DatabaseConfiguration {
    public static void main(String[] args) {
        dbconfig();
    }

    public static void dbconfig() {
        Properties properties = JDBCProperties.loadJdbcProperties();
        Flyway flyway = new Flyway();
        flyway.setDataSource(
                properties.getProperty("jdbc.url"),
                properties.getProperty("jdbc.username"),
                properties.getProperty("jdbc.password")
        );
        flyway.migrate();
    }
}
