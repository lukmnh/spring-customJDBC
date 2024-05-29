package com.project.spring.Dao;

import java.sql.Connection;
import java.util.Map;

import com.project.spring.Model.RequestLogin;
import com.project.spring.Model.ResponseLogin;
import com.project.spring.Model.Role;

public interface UserDao {

    public Map<String, Object> saveUser(Connection con, Map<String, Object> data) throws Exception;

    public Map<String, Object> getRoleById(Connection con) throws Exception;

    public ResponseLogin login(Connection con, RequestLogin data) throws Exception;
}
