package com.project.spring.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.spring.Service.RequestTravelManagementService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping(value = "/api")
public class RequestTravelManagementController {

    @Autowired
    private RequestTravelManagementService requestTravelManagementService;

    @PostMapping("/requestTravel")
    public ResponseEntity<Map<String, Object>> insertTravelRequest(@RequestBody Map<String, Object> param) {
        try {
            Map<String, Object> response = requestTravelManagementService.insertRequest(param);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "failure", "message", "Failed to insert travel request"));
        }
    }

    @PostMapping("/findHistoryTravel")
    public ResponseEntity<List<Map<String, Object>>> findHistory(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        log.info("email : {}", email);
        try {
            List<Map<String, Object>> resp = requestTravelManagementService.getHistoryByEmail(email);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/approve")
    public ResponseEntity<Map<String, Object>> approveTravelRequest(@RequestBody Map<String, Object> payload) {
        String email = (String) payload.get("email");
        try {
            Map<String, Object> response = requestTravelManagementService.approveTravelRequest(email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
