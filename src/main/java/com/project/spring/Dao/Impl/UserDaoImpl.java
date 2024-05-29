package com.project.spring.Dao.Impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.project.spring.Dao.UserDao;
import com.project.spring.Model.RequestLogin;
import com.project.spring.Model.ResponseLogin;
import com.project.spring.connection.DbConfig;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class UserDaoImpl extends DbConfig implements UserDao {

    @Override
    public Map<String, Object> saveUser(Connection con, Map<String, Object> data) throws Exception {
        Map<String, Object> result = new HashMap<>();
        String insertDataUser = "INSERT INTO public.tbl_tr_user(id_user, password, role_id) VALUES (?,?,?)";
        PreparedStatement psUser = null;
        try {
            Map<String, Object> employee = (Map<String, Object>) data.get("employee");
            Map<String, Object> role = (Map<String, Object>) data.get("role");
            psUser = con.prepareStatement(insertDataUser);
            psUser.setLong(1, (Long) employee.get("id"));
            psUser.setString(2, (String) data.get("password"));
            psUser.setInt(3, (Integer) role.get("id"));
            int rowsAffected = psUser.executeUpdate();
            result.put("data", rowsAffected);
            log.info("data insert : {}", result);
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
    public Map<String, Object> getRoleById(Connection con) throws Exception {
        String selectRoleById = "SELECT id_role, role_desc from public.tbl_m_role where role_level in (select min(role_level) from public.tbl_m_role)";
        Map<String, Object> role = new HashMap<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(selectRoleById);

            rs = ps.executeQuery();
            if (rs.next()) {
                role.put("id", rs.getInt("id_role"));
                role.put("name", rs.getString("role_desc"));
            }
        } catch (SQLException e) {
            log.error("failed to fetch role of the id role : {}", selectRoleById, e);
        } finally {
            closeStatement(rs, ps);
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

    public String getPasswordByEmail(String email) throws SQLException {
        String password = null;
        Connection con = this.getConnection();
        String query = "SELECT ttu.password FROM public.tbl_tr_user ttu "
                + "JOIN public.tbl_m_employee tme ON ttu.id_user = tme.id_employee "
                + "WHERE tme.email = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    password = rs.getString("password");
                }
            }
        }
        return password;
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
