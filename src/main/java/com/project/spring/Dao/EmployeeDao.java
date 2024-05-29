package com.project.spring.Dao;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.project.spring.Model.Employee;
import com.project.spring.Model.ResponseManagerId;
import com.project.spring.Model.ResponseRegister;

public interface EmployeeDao {
    public Map<String, Object> register(Connection con, Map<String, Object> data) throws Exception;

    public Map<String, Object> getEmployeeById(Connection con, int id) throws Exception;

    public List<ResponseManagerId> getDataEmployee(Connection con, Long id) throws Exception;
}
