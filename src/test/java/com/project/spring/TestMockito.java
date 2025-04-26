package com.project.spring;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.spring.Dao.Impl.RequestTravelManagementDaoImpl;
import com.project.spring.Service.Impl.RequestTravelManagementServiceImpl;
import com.project.spring.connection.ConnectionManager;

@ExtendWith(MockitoExtension.class)
public class TestMockito {

    @Mock
    private ConnectionManager connectionManager;

    @Mock
    private RequestTravelManagementDaoImpl dao;

    @InjectMocks
    private RequestTravelManagementServiceImpl service;

    @Test
    void testGetHistoryByEmail() throws Exception {
        String email = "lia@freelance.co.id";
        int travelId = 15;

        List<Map<String, Object>> mockHistory = new ArrayList<>();
        Map<String, Object> record = new HashMap<>();
        record.put("status", "Approved Request");
        record.put("date", "2024-05-31");
        mockHistory.add(record);

        // mock connection
        Connection mockConn = mock(Connection.class);

        // when
        when(connectionManager.getConnection()).thenReturn(mockConn);
        when(dao.findIdTravelByEmail(eq(email), eq(mockConn))).thenReturn(travelId);
        when(dao.findLastHistoryTravel(eq(email), eq(travelId), eq(mockConn))).thenReturn(mockHistory);

        // then
        List<Map<String, Object>> result = service.getHistoryByEmail(email);

        // assert
        System.out.println("Result: " + result); // Print output
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Approved Request", result.get(0).get("status"));
    }
}
