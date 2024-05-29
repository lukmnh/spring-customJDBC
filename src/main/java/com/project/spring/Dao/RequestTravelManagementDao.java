package com.project.spring.Dao;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public interface RequestTravelManagementDao {
    public Map<String, Object> insertRequest(Map<String, Object> param, Connection con) throws Exception;

    public Map<String, Object> insertStatus(Map<String, Object> param, Connection con) throws Exception;

    public int findIdByEmail(String email, Connection con) throws Exception;

}
