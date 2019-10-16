package com.code.rvlbank.services;

import org.jooq.DSLContext;

public interface IDatabaseManager {
    DSLContext getSqlDSL();
    void close();
}
