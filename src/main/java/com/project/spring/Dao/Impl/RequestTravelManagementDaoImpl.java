package com.project.spring.Dao.Impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.project.spring.Dao.RequestTravelManagementDao;
import com.project.spring.connection.DbConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class RequestTravelManagementDaoImpl extends DbConfig implements RequestTravelManagementDao {

    @Override
    public Map<String, Object> insertRequest(Map<String, Object> param, Connection con) throws Exception {
        Map<String, Object> result = new HashMap<>();
        String insertData = "insert into public.tbl_tr_travelexpense (start_location_at, location_ended_at, start_journey_date, end_journey_date, description, email, created_date)\n"
                +
                "values (?, ?, ?, ?, ?, ?, now())";
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(insertData);
            ps.setString(1, param.get("start_location_at") != null ? param.get("start_location_at").toString() : null);
            ps.setString(2, param.get("location_ended_at") != null ? param.get("location_ended_at").toString() : null);
            ps.setDate(3,
                    param.get("start_journey_date") != null
                            ? java.sql.Date.valueOf(param.get("start_journey_date").toString())
                            : null);
            ps.setDate(4,
                    param.get("end_journey_date") != null
                            ? java.sql.Date.valueOf(param.get("end_journey_date").toString())
                            : null);
            ps.setString(5, param.get("description") != null ? param.get("description").toString() : null);
            ps.setString(6, param.get("email") != null ? param.get("email").toString() : null);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                result.put("status", "success");
                result.put("message", "Travel request inserted successfully.");
            } else {
                result.put("status", "failure");
                result.put("message", "Failed to insert travel request.");
            }

            log.info("Inserted travel request: {}", param);
        } catch (Exception e) {
            log.error("Failed to request", e);
            throw new Exception("Failed to request travel", e);
        } finally {
            closeStatement(null, ps);
        }
        return result;
    }

}
