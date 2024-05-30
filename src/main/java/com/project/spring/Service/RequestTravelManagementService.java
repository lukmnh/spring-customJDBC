package com.project.spring.Service;

import java.util.List;
import java.util.Map;

public interface RequestTravelManagementService {
    public Map<String, Object> insertRequest(Map<String, Object> param) throws Exception;

    public List<Map<String, Object>> getHistoryByEmail(String email) throws Exception;

    public Map<String, Object> approveTravelRequest(String email) throws Exception;
}
