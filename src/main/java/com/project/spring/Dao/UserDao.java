package com.project.spring.Dao;

import java.sql.Connection;

import com.project.spring.Model.RequestLogin;
import com.project.spring.Model.ResponseLogin;
import com.project.spring.Model.ResponseRegister;
import com.project.spring.Model.Role;
import com.project.spring.Model.User;

public interface UserDao {

    public User saveUser(Connection con, User data) throws Exception;

    public Role getRoleById(Connection con, int id) throws Exception;

    public ResponseLogin login(Connection con, RequestLogin data) throws Exception;
}
