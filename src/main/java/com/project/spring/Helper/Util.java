package com.project.spring.Helper;

import java.sql.Connection;
import java.sql.SQLException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Util {

    public static Boolean isConnectionAvail(Connection connection) throws Exception {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("Error checking connection availability: " + e.getMessage(), e);
            return false;
        }
    }

}
