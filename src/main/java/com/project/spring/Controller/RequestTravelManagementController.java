package com.project.spring.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.spring.Service.RequestTravelManagementService;

@RestController
@RequestMapping(value = "/api")
public class RequestTravelManagementController {

    @Autowired
    private RequestTravelManagementService requestTravelManagementService;

    @PostMapping("/insert")
    public ResponseEntity<Map<String, Object>> insertTravelRequest(@RequestBody Map<String, Object> param) {
        try {
            Map<String, Object> response = requestTravelManagementService.insertRequest(param);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "failure", "message", "Failed to insert travel request"));
        }
    }
}
