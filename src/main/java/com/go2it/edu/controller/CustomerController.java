package com.go2it.edu.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.go2it.edu.dto.Customer;
import com.go2it.edu.utils.CustomerUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping(value = "/customers")
public class CustomerController {

    @GetMapping
    public ResponseEntity getAllCustomers() throws JsonProcessingException {

        List<Customer> customers = CustomerUtils.getCustomers();
        if (customers.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return new ResponseEntity(objectMapper.writeValueAsString(customers), HttpStatus.OK);
    }
}
