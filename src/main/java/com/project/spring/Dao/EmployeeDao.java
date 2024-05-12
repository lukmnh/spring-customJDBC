package com.project.spring.Dao;

import java.sql.Connection;
import java.util.Map;

import com.project.spring.Model.Employee;
import com.project.spring.Model.ResponseRegister;

public interface EmployeeDao {
    public Employee register(Connection con, Employee data) throws Exception;

    public String getDataByEmail(String email, Connection con) throws Exception;
}
