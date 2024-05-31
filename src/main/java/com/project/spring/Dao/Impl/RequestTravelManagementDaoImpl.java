package com.project.spring.Dao.Impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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
        String insertData = "INSERT INTO public.tbl_m_status_tracking (status, date, employee_id, travel_id, current_status) VALUES (?, current_timestamp, ?, ?)";
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(insertData);
            ps.setString(1, param.get("status") != null ? param.get("status").toString() : null);
            ps.setInt(2, param.get("employee_id") != null ? (Integer) param.get("employee_id") : null);
            ps.setInt(3, param.get("travel_id") != null ? (Integer) param.get("travel_id") : null);
            ps.setString(4, param.get("current_status") != null ? param.get("current_status").toString() : null);

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
        int result = -1;
        // String getIdTravel = "select id_travel from tbl_tr_travelexpense where email
        // = ? and order by created_date desc limit 1";
        String getIdTravel = "select t.id_travel\n" +
                "from tbl_m_status_tracking s\n" +
                "join tbl_tr_travelexpense t on s.travel_id = t.id_travel\n" +
                "where t.email = ?\n" +
                "order by t.created_date desc limit 1\n";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(getIdTravel);
            ps.setString(1, email);
            rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getInt("id_travel");
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

    @Override
    public List<Map<String, Object>> findLastHistoryTravel(String email, int travelId, Connection con)
            throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();
        String findHistory = "WITH FindHistory AS (\n" +
                "SELECT\n" +
                "st.employee_id,\n" +
                "st.travel_id,\n" +
                "st.status,\n" +
                "st.date,\n" +
                "emp.id_employee,\n" +
                "emp.fullname AS employee_name,\n" +
                "emp.email,\n" +
                "te.id_travel,\n" +
                "te.start_location_at AS start_location,\n" +
                "te.location_ended_at AS end_location,\n" +
                "te.description AS travel_description,\n" +
                "ROW_NUMBER() OVER (PARTITION BY st.employee_id, st.travel_id ORDER BY st.date DESC) AS rn\n" +
                "FROM\n" +
                "tbl_m_status_tracking st\n" +
                "JOIN\n" +
                "tbl_m_employee emp ON st.employee_id = emp.id_employee\n" +
                "JOIN\n" +
                "tbl_tr_travelexpense te ON st.travel_id = te.id_travel\n" +
                "WHERE\n" +
                "emp.email = ?\n" +
                "AND st.travel_id = ?\n" +
                ")\n" +
                "SELECT\n" +
                "employee_name,\n" +
                "start_location,\n" +
                "end_location,\n" +
                "travel_description,\n" +
                "date,\n" +
                "status\n" +
                "FROM\n" +
                "FindHistory\n" +
                "WHERE\n" +
                "rn <= 3\n" +
                "ORDER BY\n" +
                "employee_id, travel_id, date ASC;\n";
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(findHistory);
            ps.setString(1, email);
            ps.setInt(2, travelId);
            log.info("Set email parameter: {}", email);
            log.info("Set travelId parameter: {}", travelId);
            rs = ps.executeQuery();
            log.info("Executed query, checking results...");
            con.commit();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                log.info("Found results for email: {}", email);
                row.put("employee_name", rs.getString("employee_name"));
                row.put("start_location", rs.getString("start_location"));
                row.put("end_location", rs.getString("end_location"));
                row.put("travel_description", rs.getString("travel_description"));
                row.put("date", rs.getString("date"));
                row.put("status", rs.getString("status"));
                result.add(row);
            }

            log.info("findHistory : {}", result);
        } catch (Exception e) {
            log.error("failed to fetch history", e);
            throw new Exception("failed to fetch history from database", e);
        } finally {
            closeStatement(rs, ps);
        }
        return result;
    }

    @Override
    public Map<String, Object> approvalStatus(String email, int managerId, int travelId, Connection con)
            throws Exception {
        Map<String, Object> responseApprove = new HashMap<>();
        String updateStatus = "WITH employee_data AS (\n" +
                "SELECT\n" +
                "e.id_employee\n" +
                "FROM\n" +
                "tbl_m_employee e\n" +
                "WHERE\n" +
                "e.manager_id = ?\n" +
                "AND e.email = ?\n" +
                ")\n" +
                "INSERT INTO tbl_m_status_tracking (employee_id, status, date, current_status, travel_id)\n" +
                "SELECT\n" +
                "ed.id_employee,\n" +
                "'Approved Request',\n" +
                "NOW(),\n" +
                "'Request Approved, Have a safe trip!',\n" +
                "?\n" +
                "FROM\n" +
                "employee_data ed\n";

        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(updateStatus);
            ps.setInt(1, managerId);
            ps.setString(2, email);
            ps.setInt(3, travelId);
            // Execute the CTE query to get the employee_id dynamically
            String employeeQuery = "SELECT e.id_employee " +
                    "FROM tbl_m_employee e " +
                    "WHERE e.manager_id = ? " +
                    "AND e.email = ?";
            PreparedStatement psEmployee = con.prepareStatement(employeeQuery);
            psEmployee.setInt(1, managerId);
            psEmployee.setString(2, email);
            rs = psEmployee.executeQuery();

            if (rs.next()) {
                int employeeId = rs.getInt("id_employee");
                String status = "Approved Request";
                String currentStatus = "Request Approved, Have a safe trip!";
                Timestamp currentDate = new Timestamp(System.currentTimeMillis());

                // Log the data before execution
                log.info("Executing INSERT with the following data:");
                log.info("Employee ID: {}", employeeId);
                log.info("Manager ID: {}", managerId);
                log.info("Email: {}", email);
                log.info("Travel ID: {}", travelId);
                log.info("Status: {}", status);
                log.info("Current Status: {}", currentStatus);
                log.info("Date: {}", currentDate);

                int rowsInserted = ps.executeUpdate();
                if (rowsInserted > 0) {
                    responseApprove.put("status", "success");
                    responseApprove.put("message", "Status inserted successfully.");
                    con.commit();
                    responseApprove.put("data inserted", rowsInserted);

                    log.info("Status inserted successfully, rows inserted: {}", rowsInserted);
                } else {
                    responseApprove.put("status", "failure");
                    responseApprove.put("message", "Failed to insert status.");
                    con.rollback();

                    log.info("Failed to insert status, rolling back transaction.");
                }
            } else {
                responseApprove.put("status", "failure");
                responseApprove.put("message", "Employee not found.");
                log.info("Employee not found with manager ID: {} and email: {}", managerId, email);
            }
        } catch (Exception e) {
            log.error("Failed to update status", e);
            con.rollback();
            throw new Exception("Failed to update status travel", e);
        } finally {
            closeStatement(null, ps);
        }
        return responseApprove;
    }

    @Override
    public int findManagerIdByEmail(String email, Connection con) throws Exception {
        int managerId = -1;
        String query = "SELECT manager_id FROM tbl_m_employee WHERE email = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    managerId = rs.getInt("manager_id");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Failed to retrieve managerId: " + e.getMessage(), e);
        }
        return managerId;
    }

}

// after the approval move to settlement and create expense table