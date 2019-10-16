package com.code.rvlbank.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class JDBCProperties {

    public static Properties loadJdbcProperties()  {
        try {
            InputStream propsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db/db.properties");
            Properties properties = new Properties();
            properties.load(propsStream);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Error loading JDBC properties", e);
        }
    }
}
