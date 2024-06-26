package com.project.spring.Service.Impl;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.spring.Dao.Impl.UserDaoImpl;
import com.project.spring.Helper.Util;
import com.project.spring.Model.RequestLogin;
import com.project.spring.Model.ResponseLogin;
import com.project.spring.Service.UserService;
import com.project.spring.connection.DbConfig;

@Service
public class UserServiceImpl extends DbConfig implements UserService {

    @Autowired
    private UserDaoImpl user;

    @Override
    public Map<String, Object> login(RequestLogin data) throws Exception {
        Map<String, Object> result = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        Connection con = null;
        try {
            if (!Util.isConnectionAvail(con)) {
                con = this.getConnection();
            }
            ResponseLogin responseLogin = user.login(con, data);
            result.put("data", responseLogin);
            String jsonResponse = mapper.writeValueAsString(responseLogin);
            log.info("Response: " + jsonResponse);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception("error", e);
        } finally {
            closeConnection(con);
        }
        return result;
    }

}
