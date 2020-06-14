package com.go2it.edu.utils;

import com.go2it.edu.dto.Customer;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class CustomerUtils {

    public static List<Customer> getCustomers() {
        List<Customer> customerList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Customer customer = new Customer(String.valueOf(((char) (i + 30))), LocalDate.of(2020, Month.APRIL, i + 4), "random address " + i);
            customerList.add(customer);
        }
        return customerList;
    }
}
