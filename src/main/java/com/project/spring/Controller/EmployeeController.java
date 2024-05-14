package com.project.spring.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.project.spring.Model.RequestRegister;
import com.project.spring.Service.Impl.EmployeeServiceImpl;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class EmployeeController {

    @Autowired
    EmployeeServiceImpl service;

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> saveRegis(@RequestBody RequestRegister param) {
        Map<String, Object> response = new HashMap<>();
        try {
            // ObjectMapper mapper = new ObjectMapper();
            // String json = mapper.writeValueAsString(param); // Convert param to JSON
            // string
            // ResponseRegister register = mapper.readValue(json, ResponseRegister.class);
            response = service.dataEmployeeUser(param);
            response.put("message", "insert data success");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/dataEmployee", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getData(@RequestBody Map<String, Long> param) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long id = param.get("id");
            if (id == null) {
                response.put("message", "You are trying to access without inputting the employee ID");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            response = service.getDataEmployee(id);
            if (response.get("data") == null) {
                response.put("message", "The ID you are trying to access is not matching");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
