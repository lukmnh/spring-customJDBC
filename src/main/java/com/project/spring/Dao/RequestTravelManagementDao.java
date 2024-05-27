package com.project.spring.Dao;

import java.sql.Connection;
import java.util.Map;

public interface RequestTravelManagementDao {
    public Map<String, Object> insertRequest(Map<String, Object> param, Connection con) throws Exception;
}
