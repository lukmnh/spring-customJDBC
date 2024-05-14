package com.project.spring.Service;

import java.sql.Connection;
import java.util.Map;

import com.project.spring.Model.RequestLogin;
import com.project.spring.Model.ResponseRegister;
import com.project.spring.Model.User;

public interface UserService {
    public Map<String, Object> login(RequestLogin data) throws Exception;
}
