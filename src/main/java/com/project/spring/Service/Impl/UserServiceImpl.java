package com.project.spring.Service.Impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.spring.Dao.UserDao;
import com.project.spring.Helper.Util;
import com.project.spring.Model.RequestLogin;
import com.project.spring.Model.ResponseLogin;
import com.project.spring.Service.UserService;
import com.project.spring.connection.DbConfig;

@Service
public class UserServiceImpl extends DbConfig implements UserService, UserDetailsService {

    @Autowired
    private UserDao user;

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
            if (responseLogin == null || !data.getPassword().equals(hashPassword(data.getPassword()))) {
                throw new Exception("Invalid email or password");
            }
            result.put("data", responseLogin);
            // Convert response to JSON string and log it
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

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try (Connection con = getConnection()) {
            RequestLogin data = new RequestLogin();
            data.setEmail(email);
            ResponseLogin responseLogin = user.login(con, data);
            if (responseLogin == null) {
                throw new UsernameNotFoundException("User not found with email: " + email);
            }
            return new org.springframework.security.core.userdetails.User(
                    responseLogin.getEmail(),
                    data.getPassword(),
                    getAuthorities(responseLogin.getRoleDesc()));
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found with email: " + email, e);
        }
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String roleDesc) {
        return List.of(new SimpleGrantedAuthority(roleDesc));
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(password.getBytes());

        StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
        for (byte b : encodedHash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
