package com.project.spring.Dao.Impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.project.spring.Dao.EmployeeDao;
import com.project.spring.Model.Employee;
import com.project.spring.Model.ResponseManagerId;
import com.project.spring.Model.Role;
import com.project.spring.Model.User;
import com.project.spring.connection.DbConfig;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class EmployeeDaoImpl extends DbConfig implements EmployeeDao {

    @Override
    public Map<String, Object> register(Connection con, Map<String, Object> data) throws Exception {
        Map<String, Object> result = new HashMap<>();
        String insertDataEmployee = "INSERT INTO public.tbl_m_employee(fullname, email, bod, address, manager_id) VALUES (?,?,?,?,?)";
        PreparedStatement psEmployee = null;
        try {
            psEmployee = con.prepareStatement(insertDataEmployee, Statement.RETURN_GENERATED_KEYS);
            // insert employee data
            psEmployee.setString(1, (String) data.get("fullname"));
            psEmployee.setString(2, (String) data.get("email"));
            LocalDate localDate = (LocalDate) data.get("bod");
            Date sqlDate = Date.valueOf(localDate);
            psEmployee.setDate(3, sqlDate);
            psEmployee.setString(4, (String) data.get("address"));
            if (data.get("managerId") != null) {
                psEmployee.setInt(5, (int) data.get("managerId"));
            } else {
                psEmployee.setNull(5, Types.INTEGER);
            }
            psEmployee.executeUpdate();

            ResultSet rs = psEmployee.getGeneratedKeys();
            if (rs.next()) {
                data.put("id", rs.getLong(1));
            }
            result = data;
            con.commit();

        } catch (SQLException e) {
            log.error("Failed to register employee", e);
            throw new Exception("Failed to register employee", e);
        } finally {
            closeStatement(rs, psEmployee);
        }
        return result;
    }

    @Override
    public Map<String, Object> getEmployeeById(Connection con, int id) throws Exception {
        String selectEmployeeById = "SELECT id_employee, fullname, email, bod, address, manager_id FROM public.tbl_m_employee WHERE id_employee = ?";
        Map<String, Object> employee = new HashMap<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(selectEmployeeById);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                employee.put("id", rs.getLong("id_employee"));
                employee.put("fullname", rs.getString("fullname"));
                employee.put("email", rs.getString("email"));
                employee.put("bod", rs.getTimestamp("bod").toLocalDateTime());
                employee.put("address", rs.getString("address"));
                employee.put("managerId", rs.getLong("manager_id"));
            }
        } catch (SQLException e) {
            log.error("failed to fetch data employee", e);
            throw new Exception("failed to fetch data from database", e);
        } finally {
            closeStatement(rs, ps);
        }
        return employee;
    }

    @Override
    public List<ResponseManagerId> getDataEmployee(Connection con, Long id) throws Exception {
        List<ResponseManagerId> response = new ArrayList<>();
        Role role = new Role();
        User user = new User();
        Employee manager = new Employee();
        ResponseManagerId resp = new ResponseManagerId();
        String query = "SELECT tme.id_employee, tme.fullname, tme.email, tme.bod, tme.address, " +
                "tmr.role_desc, " +
                "(SELECT mgr.fullname FROM public.tbl_m_employee mgr WHERE mgr.id_employee = tme.manager_id) AS manager_name, "
                +
                "(select mgr.email from public.tbl_m_employee mgr where mgr.id_employee = tme.manager_id) as manager_email "
                +
                "FROM public.tbl_m_employee tme " +
                "JOIN public.tbl_tr_user ttu ON tme.id_employee = ttu.id_user " +
                "JOIN public.tbl_m_role tmr ON ttu.role_id = tmr.id_role " +
                "WHERE tme.id_employee = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query);
            ps.setLong(1, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                resp.setId(rs.getLong("id_employee"));
                resp.setFullname(rs.getString("fullname"));
                resp.setEmail(rs.getString("email"));
                resp.setBod(rs.getTimestamp("bod").toLocalDateTime());
                resp.setAddress(rs.getString("address"));
                role.setName(rs.getString("role_desc"));
                user.setRole(role);
                resp.setUser(user);
                manager.setFullname(rs.getString("manager_name"));
                manager.setEmail(rs.getString("manager_email"));
                resp.setManagerId(manager);
                response.add(resp);
            }
        } catch (Exception e) {
            log.error("failed to fetch data", e);
            throw new Exception("failed to fetch data from database", e);
        } finally {
            closeStatement(rs, ps);
        }
        return response;
    }

}
