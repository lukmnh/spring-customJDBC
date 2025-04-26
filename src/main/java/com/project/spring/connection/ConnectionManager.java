package com.project.spring.connection;

import java.sql.Connection;

public interface ConnectionManager {
    Connection getConnection();

    void closeConnection(Connection con);
}
