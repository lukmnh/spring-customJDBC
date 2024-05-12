package com.project.spring.connection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DbConfig {

    @Autowired
    protected DataSource dataSource;

    protected static final Logger log = LoggerFactory.getLogger(DbConfig.class);

    protected Connection connection = null;
    protected PreparedStatement ps = null;
    protected ResultSet rs = null;

    public void connect() {
        try {
            connection = dataSource.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
    }

    public Connection getConnection() {
        try {
            if (this.connection == null) {
                this.connect();
            } else if (this.connection.isClosed()) {
                this.connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
        return this.connection;
    }

    public void closeConnection(Connection con) {
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
    }

    public void closeStatement(ResultSet rs, PreparedStatement ps) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
    }
}
