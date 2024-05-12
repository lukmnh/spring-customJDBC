package com.project.spring.Service;

import java.util.Map;

import com.project.spring.Model.RequestRegister;
import com.project.spring.Model.ResponseRegister;

public interface EmployeeService {

    public Map<String, Object> dataEmployeeUser(RequestRegister data) throws Exception;
}
