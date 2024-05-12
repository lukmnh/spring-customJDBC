package com.project.spring.Dao.Impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.springframework.stereotype.Repository;

import com.project.spring.Dao.EmployeeDao;
import com.project.spring.Model.Employee;
import com.project.spring.Model.RequestRegister;
import com.project.spring.connection.DbConfig;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class EmployeeDaoImpl extends DbConfig implements EmployeeDao {

    @Override
    public Employee register(Connection con, Employee data) throws Exception {
        RequestRegister result = new RequestRegister();
        String insertDataEmployee = "insert into public.tbl_m_employee(id_employee, fullname, email, bod, address, manager_id) values (?,?,?,?,?,?)";
        // String insertDataUser = "insert into public.tbl_tr_user(id_user, password,
        // role_id) values (?,?,?)";
        // PreparedStatement psUser = con.prepareStatement(insertDataUser))
        try (PreparedStatement psEmployee = con.prepareStatement(insertDataEmployee,
                Statement.RETURN_GENERATED_KEYS);) {

            // insert employee data
            psEmployee.setLong(1, data.getId());
            psEmployee.setString(2, data.getFullname());
            psEmployee.setString(3, data.getEmail());
            psEmployee.setTimestamp(4, Timestamp.valueOf(data.getBod()));
            psEmployee.setString(5, data.getAddress());
            psEmployee.setLong(6, data.getManagerId().getId());

            psEmployee.executeUpdate();
            con.commit();

            // try (ResultSet rs = psEmployee.getGeneratedKeys()) {
            // if (rs.next()) {
            // long employeeId = rs.getLong(1);
            // psUser.setLong(1, employeeId);
            // psUser.setString(2, result.getPassword());
            // psUser.setInt(3, result.getRole().getId());
            // psUser.executeUpdate();
            // result.setId(employeeId);
            // } else {
            // throw new SQLException("Inserting employee data failed, no generated keys
            // obtained.");
            // }
            // }

        } catch (SQLException e) {
            log.error("Failed to register employee", e);
            throw new Exception("Failed to register employee", e);
        }
        return data;
    }

    @Override
    public String getDataByEmail(String email, Connection con) throws Exception {
        return email;
    }

}
