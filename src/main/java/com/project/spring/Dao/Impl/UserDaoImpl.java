package com.project.spring.Dao.Impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

import com.project.spring.Dao.UserDao;
import com.project.spring.Model.RequestLogin;
import com.project.spring.Model.ResponseLogin;
import com.project.spring.Model.User;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class UserDaoImpl implements UserDao {

    @Override
    public User saveUser(Connection con, User data) throws Exception {
        String insertDataUser = "INSERT INTO public.tbl_tr_user(id_user, password, role_id) VALUES (?,?,?)";
        try (PreparedStatement psUser = con.prepareStatement(insertDataUser)) {
            psUser.setLong(1, data.getEmployee().getId());
            psUser.setString(2, data.getPassword());
            psUser.setInt(3, data.getRole().getId());
            psUser.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            log.error("Failed to register employee", e);
            throw new Exception("Failed to register employee", e);
        }
        return data;
    }

    @Override
    public ResponseLogin login(Connection con, RequestLogin data) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'login'");
    }

}
