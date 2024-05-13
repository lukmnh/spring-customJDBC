package com.project.spring.Service.Impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.spring.Dao.Impl.EmployeeDaoImpl;
import com.project.spring.Dao.Impl.UserDaoImpl;
import com.project.spring.Helper.Util;
import com.project.spring.Model.Employee;
import com.project.spring.Model.RequestRegister;
import com.project.spring.Model.ResponseRegister;
import com.project.spring.Model.Role;
import com.project.spring.Model.User;
import com.project.spring.Service.EmployeeService;
import com.project.spring.connection.DbConfig;

@Service
public class EmployeeServiceImpl extends DbConfig implements EmployeeService {

    @Autowired
    private EmployeeDaoImpl emp;

    @Autowired
    private UserDaoImpl userDao;

    @Override
    public Map<String, Object> dataEmployeeUser(RequestRegister data) throws Exception {
        Map<String, Object> response = new HashMap<>();
        Connection con = null;
        try {
            if (!Util.isConnectionAvail(con)) {
                con = this.dataSource.getConnection();
            }
            con.setAutoCommit(false);
            // insert employee
            Employee employee = new Employee();
            employee.setId(data.getId());
            employee.setFullname(data.getFullname());
            employee.setEmail(data.getEmail());
            employee.setBod(data.getBod());
            employee.setAddress(data.getAddress());
            employee.setManagerId(data.getManagerId());
            Employee savedEmployee = emp.register(con, employee);

            // insert user
            User user = new User();
            user.setEmployee(savedEmployee);
            String hashedPassword = hashPassword(data.getPassword());
            user.setPassword(hashedPassword);
            user.setRole(data.getRole());
            User savedUser = userDao.saveUser(con, user);

            // fetch full details of managerId
            Employee manager = emp.getEmployeeById(con, data.getManagerId().getId());
            savedEmployee.setManagerId(manager);

            // fetch full details of role
            Role role = userDao.getRoleById(con, data.getRole().getId());
            savedUser.setRole(role);

            // response insert
            ResponseRegister resp = new ResponseRegister();
            resp.setFullname(savedEmployee.getFullname());
            resp.setEmail(savedEmployee.getEmail());
            resp.setBod(savedEmployee.getBod());
            resp.setAddress(savedEmployee.getAddress());
            resp.setManager_id(savedEmployee.getManagerId());
            resp.setRole(savedUser.getRole());
            resp.setPassword(savedUser.getPassword());
            response.put("data", resp);

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

}
