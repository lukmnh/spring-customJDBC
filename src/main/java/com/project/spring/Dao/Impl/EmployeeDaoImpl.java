package com.project.spring.Dao.Impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;

import org.springframework.stereotype.Repository;

import com.project.spring.Dao.EmployeeDao;
import com.project.spring.Model.Employee;
import com.project.spring.connection.DbConfig;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class EmployeeDaoImpl extends DbConfig implements EmployeeDao {

    // @Override
    // public Employee register(Connection con, Employee data) throws Exception {
    // RequestRegister result = new RequestRegister();
    // String insertDataEmployee = "insert into public.tbl_m_employee(id_employee,
    // fullname, email, bod, address, manager_id) values (?,?,?,?,?,?)";

    // try (PreparedStatement psEmployee = con.prepareStatement(insertDataEmployee,
    // Statement.RETURN_GENERATED_KEYS);) {

    // // insert employee data
    // psEmployee.setLong(1, data.getId());
    // psEmployee.setString(2, data.getFullname());
    // psEmployee.setString(3, data.getEmail());
    // psEmployee.setTimestamp(4, Timestamp.valueOf(data.getBod()));
    // psEmployee.setString(5, data.getAddress());
    // psEmployee.setLong(6, data.getManagerId().getId());

    // psEmployee.executeUpdate();
    // con.commit();

    // } catch (SQLException e) {
    // log.error("Failed to register employee", e);
    // throw new Exception("Failed to register employee", e);
    // }
    // return data;
    // }

    // @Override
    // public String getDataByEmail(String email, Connection con) throws Exception {
    // return email;
    // }
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
            }
        }
        return employee;
    }

}
