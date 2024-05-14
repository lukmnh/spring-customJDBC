package com.project.spring.Dao.Impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.connector.Response;
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
    public Employee register(Connection con, Employee data) throws Exception {
        String insertDataEmployee = "INSERT INTO public.tbl_m_employee(id_employee, fullname, email, bod, address, manager_id) VALUES (?,?,?,?,?,?)";

        try (PreparedStatement psEmployee = con.prepareStatement(insertDataEmployee, Statement.RETURN_GENERATED_KEYS)) {

            // insert employee data
            psEmployee.setLong(1, data.getId());
            psEmployee.setString(2, data.getFullname());
            psEmployee.setString(3, data.getEmail());
            psEmployee.setTimestamp(4, Timestamp.valueOf(data.getBod()));
            psEmployee.setString(5, data.getAddress());
            if (data.getManagerId() != null) {
                psEmployee.setLong(6, data.getManagerId());
            } else {
                psEmployee.setNull(6, Types.INTEGER);
            }

            psEmployee.executeUpdate();
            con.commit();

        } catch (SQLException e) {
            log.error("Failed to register employee", e);
            throw new Exception("Failed to register employee", e);
        }
        return data;
    }

    @Override
    public Employee getEmployeeById(Connection con, Long id) throws Exception {
        String selectEmployeeById = "SELECT id_employee, fullname, email, bod, address, manager_id FROM public.tbl_m_employee WHERE id_employee = ?";
        Employee employee = new Employee();
        try (PreparedStatement ps = con.prepareStatement(selectEmployeeById)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    employee.setId(rs.getLong("id_employee"));
                    employee.setFullname(rs.getString("fullname"));
                    employee.setEmail(rs.getString("email"));
                    employee.setBod(rs.getTimestamp("bod").toLocalDateTime());
                    employee.setAddress(rs.getString("address"));
                    employee.setManagerId(rs.getLong("manager_id"));
                }
            } catch (SQLException e) {
                log.error("failed to fetch data employee", e);
                throw new Exception("failed to fetch data from database", e);
            }
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
        }
        return response;
    }

}
