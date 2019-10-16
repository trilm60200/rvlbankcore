package com.code.rvlbank.utils;

import com.code.rvlbank.properties.JDBCProperties;
import org.jooq.codegen.GenerationTool;
import org.jooq.meta.h2.H2Database;
import org.jooq.meta.jaxb.*;

import java.util.Properties;

public class JooqClassGenerator {
    public static void main(String[] args) throws Exception {

        Configuration configuration = new Configuration();
        configuration
                .withJdbc(readJDBCProperties())
                .withGenerator(new Generator()
                        .withDatabase(new Database()
                                .withName(H2Database.class.getName())
                                .withInputSchema("PUBLIC")
                                .withExcludes("INFORMATION_SCHEMA.*|FLYWAY.*"))
                        .withTarget(new Target()
                                .withPackageName("com.code.rvlbank.models.sql")
                                .withDirectory("./src/main/java")));
        (new GenerationTool()).run(configuration);
    }

    private static Jdbc readJDBCProperties() {
        Properties properties = JDBCProperties.loadJdbcProperties();
        return new Jdbc()
                    .withDriver(properties.getProperty("jdbc.driver"))
                    .withUrl(properties.getProperty("jdbc.url"))
                    .withUsername(properties.getProperty("jdbc.username"))
                    .withPassword(properties.getProperty("jdbc.password"));
    }

}
