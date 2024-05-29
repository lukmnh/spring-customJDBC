package com.project.spring.Service.Impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.spring.Dao.Impl.EmployeeDaoImpl;
import com.project.spring.Dao.Impl.UserDaoImpl;
import com.project.spring.Helper.Util;
import com.project.spring.Model.RequestRegister;
import com.project.spring.Model.ResponseManagerId;
import com.project.spring.Service.EmployeeService;
import com.project.spring.connection.DbConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
public class EmployeeServiceImpl extends DbConfig implements EmployeeService {

    @Autowired
    private EmployeeDaoImpl emp;

    @Autowired
    private UserDaoImpl userDao;

    @Override
    public Map<String, Object> dataEmployeeUser(RequestRegister data) throws Exception {
        Map<String, Object> response = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        Connection con = null;
        try {
            if (!Util.isConnectionAvail(con)) {
                con = this.dataSource.getConnection();
            }
            con.setAutoCommit(false);
            // insert employee
            Map<String, Object> employee = new HashMap<>();

            employee.put("fullname", data.getFullname());
            employee.put("email", data.getEmail());
            employee.put("bod", data.getBod());
            employee.put("address", data.getAddress());
            employee.put("managerId", data.getManagerId());
            Map<String, Object> savedEmployee = emp.register(con, employee);

            // insert user
            Map<String, Object> user = new HashMap<>();
            user.put("employee", savedEmployee);
            log.info("data : {}", user);
            String hashedPassword = hashPassword(data.getPassword());
            user.put("password", hashedPassword);
            // user.put("role", data.getRole());
            Map<String, Object> role = userDao.getRoleById(con);
            user.put("role", role);
            log.info("data : {}", user);

            Map<String, Object> savedUser = userDao.saveUser(con, user);

            // response insert
            Map<String, Object> resp = new HashMap<>();
            resp.put("fullname", savedEmployee.get("fullname"));
            resp.put("email", savedEmployee.get("email"));
            resp.put("bod", savedEmployee.get("bod"));
            resp.put("address", savedEmployee.get("address"));
            resp.put("manager_id", savedEmployee.get("managerId"));
            resp.put("role", savedUser.get("role"));
            resp.put("password", savedUser.get("password"));

            response.put("data", resp);
            mapper.registerModule(new JavaTimeModule());
            String jsonResponse = mapper.writeValueAsString(resp);
            log.info("Response: " + jsonResponse);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response.put("error", e.getMessage());
        } finally {
            closeConnection(con);
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

    @Override
    public Map<String, Object> getDataEmployee(Long id) throws Exception {
        Map<String, Object> result = new HashMap<>();
        Connection con = null;
        try {
            if (!Util.isConnectionAvail(con)) {
                con = this.getConnection();
            }
            if (id == null) {
                result.put("message", "You are trying to access without inputting the employee ID");
                return result;
            }
            List<ResponseManagerId> dataEmployee = emp.getDataEmployee(con, id);
            if (dataEmployee.isEmpty()) {
                result.put("message", "The id you are trying to access is not matching");
            } else {
                result.put("data", dataEmployee);
            }
        } catch (Exception e) {
            log.error("Error fetching data", e);
            result.put("error", e.getMessage());
        } finally {
            closeConnection(con);
        }
        return result;
    }

}
