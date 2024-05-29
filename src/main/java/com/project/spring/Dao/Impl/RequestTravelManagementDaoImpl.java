package com.project.spring.Dao.Impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        String insertData = "insert into public.tbl_tr_travelexpense (id_travel, start_location_at, location_ended_at, start_journey_date, end_journey_date, description, email, created_date)\n"
                +
                "values (nextval('public.tbl_tr_travelexpense_id_travel_seq'), ?, ?, ?, ?, ?, ?, current_timestamp)";
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(insertData);
            ps.setString(1, param.get("start_location_at") != null ? param.get("start_location_at").toString() : null);
            ps.setString(2, param.get("location_ended_at") != null ? param.get("location_ended_at").toString() : null);
            ps.setTimestamp(3,
                    param.get("start_journey_date") != null
                            ? new Timestamp(sdf(param.get("start_journey_date").toString()).getTime())
                            : null);
            ps.setTimestamp(4,
                    param.get("end_journey_date") != null
                            ? new Timestamp(sdf(param.get("end_journey_date").toString()).getTime())
                            : null);
            ps.setString(5, param.get("description") != null ? param.get("description").toString() : null);
            ps.setString(6, param.get("email") != null ? param.get("email").toString() : null);
            result.putAll(param);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                result.put("status", "success");
                result.put("message", "Travel request inserted successfully.");
            } else {
                result.put("status", "failure");
                result.put("message", "Failed to insert travel request.");
            }
            con.commit();
            log.info("Inserted travel request: {}", param);
        } catch (Exception e) {
            log.error("Failed to request", e);
            throw new Exception("Failed to request travel", e);
        } finally {
            closeStatement(null, ps);
        }
        return result;
    }

    @Override
    public Map<String, Object> insertStatus(Map<String, Object> param, Connection con) throws Exception {
        Map<String, Object> result = new HashMap<>();
        String insertData = "INSERT INTO public.tbl_m_status_tracking (status, date, employee_id, travel_id) VALUES (?, current_timestamp, ?, ?)";
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(insertData);
            ps.setString(1, param.get("status") != null ? param.get("status").toString() : null);
            ps.setInt(2, param.get("employee_id") != null ? (Integer) param.get("employee_id") : null);
            ps.setInt(3, param.get("travel_id") != null ? (Integer) param.get("travel_id") : null);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                result.put("status", "success");
                result.put("message", "Status inserted successfully.");
                con.commit();
            } else {
                result.put("status", "failure");
                result.put("message", "Failed to insert status.");
                con.rollback();
            }

            log.info("Inserted status: {}", param);
        } catch (Exception e) {
            log.error("Failed to insert status", e);
            throw new Exception("Failed to insert status", e);
        } finally {
            closeStatement(null, ps);
        }
        return result;
    }

    @Override
    public int findIdByEmail(String email, Connection con) throws Exception {
        int result = 0;
        String getId = "select id_employee from tbl_m_employee where email = ? limit 1";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(getId);
            ps.setString(1, email);
            rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getInt(1);
            }

        } catch (Exception e) {
            log.error("failed to fetch id", e);
            throw new Exception("failed to fetch id from table employee", e);
        } finally {
            closeStatement(rs, ps);
        }
        return result;
    }

    @Override
    public int findIdTravelByEmail(String email, Connection con) throws Exception {
        int result = 0;
        String getIdTravel = "select id_travel from tbl_tr_travelexpense where email = ? order by created_date desc limit 1";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(getIdTravel);
            ps.setString(1, email);
            rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getInt(1);
            }

        } catch (Exception e) {
            log.error("failed to fetch id", e);
            throw new Exception("failed to fetch id from table travelexpense", e);
        } finally {
            closeStatement(rs, ps);
        }
        return result;
    }

    private Date sdf(String dateStr) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return new Date(dateFormat.parse(dateStr).getTime());
    }

}
