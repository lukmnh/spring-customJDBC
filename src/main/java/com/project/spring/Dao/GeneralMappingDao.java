package com.project.spring.Dao;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public interface GeneralMappingDao {

    public List<Map<String, Object>> findAllStatusMappingRequest(Connection con) throws Exception;

}
