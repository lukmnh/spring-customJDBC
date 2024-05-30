package com.project.spring;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.spring.Dao.Impl.RequestTravelManagementDaoImpl;
import com.project.spring.Service.Impl.RequestTravelManagementServiceImpl;

@ExtendWith(MockitoExtension.class)
public class TestTravelRequest {

    @Mock
    private RequestTravelManagementDaoImpl rtmDao;
    @Mock
    private Connection con;
    @InjectMocks
    private RequestTravelManagementServiceImpl rtmService;

    @BeforeEach
    public void setUp() {
        con = mock(Connection.class);
    }

    @Test
    public void testGetHistoryByEmail() throws Exception {
        // Mock the response from the DAO method
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("fullname", "John Doe");
        mockResponse.put("start_location_at", "Location A");
        mockResponse.put("location_ended_at", "Location B");
        mockResponse.put("description", "Travel description");
        mockResponse.put("date", "2024-05-30");
        mockResponse.put("status", "Completed");

        // Mock the behavior of the DAO method
        when(rtmDao.findLastHistoryTravel(eq("test@example.com"), any(Connection.class)))
                .thenThrow(new Exception("Database error")); // Update the email parameter

        // Mock the behavior of the connection
        when(con.getAutoCommit()).thenReturn(false);

        // Call the method under test
        String email = "test@example.com";
        Map<String, Object> result = rtmService.getHistoryByEmail(email);

        // Verify the results
        assertNotNull(result);
        assertEquals("John Doe", result.get("fullname"));
        assertEquals("Location A", result.get("start_location_at"));
        assertEquals("Location B", result.get("location_ended_at"));
        assertEquals("Travel description", result.get("description"));
        assertEquals("2024-05-30", result.get("date"));
        assertEquals("Completed", result.get("status"));

        // Verify the interactions
        verify(rtmDao).findLastHistoryTravel(anyString(), eq(con));
        verify(con).setAutoCommit(false);
        verify(con).commit();
        verify(con).close();
    }

    @Test
    public void testGetHistoryByEmail_Exception() throws Exception {
        // Mock the behavior of the DAO method to throw an exception
        when(rtmDao.findLastHistoryTravel(eq("test@example.com"), any(Connection.class)))
                .thenThrow(new Exception("Database error"));

        // Mock the behavior of the connection
        when(con.getAutoCommit()).thenReturn(false);
        doNothing().when(con).setAutoCommit(false); // Mock the setAutoCommit method

        // Call the method under test
        String email = "test@example.com";
        Exception exception = assertThrows(Exception.class, () -> {
            rtmService.getHistoryByEmail(email);
        });

        // Verify the exception message
        assertEquals("Failed to check history", exception.getMessage());

        // Verify the interactions
        verify(rtmDao).findLastHistoryTravel(eq("test@example.com"), any(Connection.class));
        verify(con).setAutoCommit(false);
        verify(con).rollback();
        verify(con).close();
        verifyNoMoreInteractions(con);
    }
}
