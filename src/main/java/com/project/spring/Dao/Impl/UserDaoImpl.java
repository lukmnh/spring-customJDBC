package com.project.spring.Dao.Impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import com.project.spring.connection.DbConfig;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class UserDaoImpl extends DbConfig implements UserDao {

    @Override
    public User saveUser(Connection con, User data) throws Exception {
        String insertDataUser = "INSERT INTO public.tbl_tr_user(id_user, password, role_id) VALUES (?,?,?)";
        PreparedStatement psUser = null;
        try {
            psUser = con.prepareStatement(insertDataUser);
            psUser.setLong(1, data.getEmployee().getId());
            psUser.setString(2, data.getPassword());
            psUser.setInt(3, data.getRole().getId());
            psUser.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            log.error("Failed to register employee", e);
            throw new Exception("Failed to register employee", e);
        } finally {
            closeStatement(null, psUser);
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
            } finally {
                closeStatement(rs, ps);
            }
        }
        return role;
    }

    @Override
    public ResponseLogin login(Connection con, RequestLogin data) throws Exception {
        ResponseLogin response = new ResponseLogin();
        String query = "select tme.fullname, tme.email, ttu.password, tmr.role_desc from public.tbl_m_employee tme\n" +
                "join public.tbl_tr_user ttu on tme.id_employee = ttu.id_user\n" +
                "join public.tbl_m_role tmr on ttu.role_id = tmr.id_role\n" +
                "where tme.email = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query);
            ps.setString(1, data.getEmail());
            rs = ps.executeQuery();
            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                String inputHashedPassword = hashPassword(data.getPassword());

                if (storedHashedPassword.equals(inputHashedPassword)) {
                    response.setName(rs.getString("fullname"));
                    response.setEmail(rs.getString("email"));
                    response.setRoleDesc(rs.getString("role_desc"));
                } else {
                    throw new Exception("invalid email password");
                }
            } else {
                throw new Exception("invalid email password");

            }
        } catch (Exception e) {
            log.error("login failed", e);
            throw new Exception(e.getMessage(), e);
        } finally {
            closeStatement(rs, ps);
        }
        return response;
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
