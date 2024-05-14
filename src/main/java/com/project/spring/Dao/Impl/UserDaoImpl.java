package com.project.spring.Dao.Impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

import com.project.spring.Dao.UserDao;
import com.project.spring.Model.RequestLogin;
import com.project.spring.Model.ResponseLogin;
import com.project.spring.Model.Role;
import com.project.spring.Model.User;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class UserDaoImpl implements UserDao {

    // @Override
    // public User saveUser(Connection con, User data) throws Exception {
    // String insertDataUser = "INSERT INTO public.tbl_tr_user(id_user, password,
    // role_id) VALUES (?,?,?)";
    // try (PreparedStatement psUser = con.prepareStatement(insertDataUser)) {
    // psUser.setLong(1, data.getEmployee().getId());
    // psUser.setString(2, data.getPassword());
    // psUser.setInt(3, data.getRole().getId());
    // psUser.executeUpdate();
    // con.commit();
    // } catch (SQLException e) {
    // log.error("Failed to register employee", e);
    // throw new Exception("Failed to register employee", e);
    // }
    // return data;
    // }

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
    public Role getRoleById(Connection con, int id) throws Exception {
        String selectRoleById = "SELECT id_role, role_desc, role_level FROM public.tbl_m_role WHERE id_role = ?";
        Role role = new Role();
        try (PreparedStatement ps = con.prepareStatement(selectRoleById)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    role.setId(rs.getInt("id_role"));
                    role.setName(rs.getString("role_desc"));
                    role.setLevel(rs.getInt("role_level"));
                }
            } catch (SQLException e) {
                log.error("failed to fetch role of the id role : {}", id, e);
            }
        }
        return role;
    }

    @Override
    public ResponseLogin login(Connection con, RequestLogin data) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'login'");
    }

}
