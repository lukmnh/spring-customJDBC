package com.project.spring.Service.Impl;

import java.sql.Connection;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.spring.Dao.RequestTravelManagementDao;
import com.project.spring.Helper.Util;
import com.project.spring.Service.RequestTravelManagementService;
import com.project.spring.connection.DbConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RequestTravelManagementServiceImpl extends DbConfig implements RequestTravelManagementService {

    @Autowired
    private RequestTravelManagementDao rtm;

    @Override
    public Map<String, Object> insertRequest(Map<String, Object> param) throws Exception {
        Map<String, Object> response = new HashMap<>();
        Connection con = null;
        try {
            if (!Util.isConnectionAvail(con)) {
                con = this.getConnection();
            }

            if (param != null && !param.isEmpty()) {
                formatDates(param);
                response = rtm.insertRequest(param, con);
                log.info("response : {}", response);
            } else {
                log.warn("Parameter map is empty or null");
                response.put("status", "failure");
                response.put("message", "Invalid input parameters");
            }
        } catch (Exception e) {
            log.error("Failed to insert travel request", e);
            throw new Exception("Failed to insert travel request", e);
        } finally {
            closeConnection(con);
        }
        return response;
    }

    private Date parseDate(String dateStr) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return new Date(dateFormat.parse(dateStr).getTime());
    }

    private void formatDates(Map<String, Object> param) throws ParseException {
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            if (entry.getValue() instanceof String) {
                String value = (String) entry.getValue();
                try {
                    Date dateValue = parseDate(value);
                    param.put(entry.getKey(), dateValue);
                } catch (ParseException e) {
                    log.error("Error parsing date for key {}: {}", entry.getKey(), value);
                }
            }
        }
    }

}
