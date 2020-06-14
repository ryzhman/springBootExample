package com.go2it.edu.controller;

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
    public ResponseEntity getAllCustomers() {

        List<Customer> customers = CustomerUtils.getCustomers();
        if (customers.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Customer c : customers) {
            sb.append("{");
            sb.append("'name': '").append(c.getName()).append("',");
            sb.append("'address': '").append(c.getAddress()).append("',");
            sb.append("'dateOfBirth': '").append(c.getDateOfBirth()).append("'").append("},");
        }
        sb.append("]");
        return new ResponseEntity(sb, HttpStatus.OK);
    }
}
