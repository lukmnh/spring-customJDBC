package com.project.spring.Service.Impl;

import java.sql.Connection;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
        Map<String, Object> statusParam = new HashMap<>();
        Connection con = null;
        try {
            if (!Util.isConnectionAvail(con)) {
                con = this.getConnection();
            }
            con.setAutoCommit(false);

            if (param != null && !param.isEmpty()) {
                // insert travel request
                formatDates(param);
                response = rtm.insertRequest(param, con);
                log.info("response form travel request : {}", response);
                // After inserting the travel request, insert the status
                if ("success".equals(response.get("status"))) {

                    statusParam.put("status", "Request");
                    statusParam.put("date", LocalDate.now());
                    // filtering the id by get the id from email (payload)
                    int employeeId = rtm.findIdByEmail(param.get("email").toString(), con);
                    statusParam.put("employee_id", employeeId);
                    int travelId = rtm.findIdTravelByEmail(param.get("email").toString(), con);
                    statusParam.put("travel_id", travelId);
                } else {
                    log.warn("Failed to insert travel request. Status insertion skipped.");
                }
                Map<String, Object> statusResponse = rtm.insertStatus(statusParam, con);
                log.info("Response from status insertion: {}", statusResponse);
                response.put("statusResponse", statusResponse);
                // Adjust the response order
                Map<String, Object> orderedResponse = new LinkedHashMap<>();
                orderedResponse.put("email", response.get("email"));
                orderedResponse.put("start_location", response.get("start_location_at"));
                orderedResponse.put("end_location", response.get("location_ended_at"));
                orderedResponse.put("start_date", response.get("start_journey_date"));
                orderedResponse.put("end_date", response.get("end_journey_date"));
                orderedResponse.put("statusResponse", statusResponse);
                response = orderedResponse;
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

    @Override
    public Map<String, Object> getHistoryByEmail(String email) throws Exception {
        Map<String, Object> response = new HashMap<>();
        Connection con = null;
        if (!Util.isConnectionAvail(con)) {
            con = this.getConnection();
        }
        con.setAutoCommit(false);

        try {
            response = rtm.findLastHistoryTravel(email, con);
        } catch (Exception e) {
            log.error("Failed to check history", e);
            throw new Exception("Failed to check history", e);
        } finally {
            closeConnection(con);
        }
        return response;
    }

    private Date parseDate(String dateStr) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
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
