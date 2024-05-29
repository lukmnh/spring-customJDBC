package com.project.spring.Service;

import java.util.Map;

public interface RequestTravelManagementService {
    public Map<String, Object> insertRequest(Map<String, Object> param) throws Exception;

    public Map<String, Object> getHistoryByEmail(String email) throws Exception;
}
