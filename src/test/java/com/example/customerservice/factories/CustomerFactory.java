package com.example.customerservice.factories;

import com.example.customerservice.model.Customer;
import com.example.customerservice.repository.CustomerRepository;
import com.example.customerservice.testHelpers.SpringContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CustomerFactory {
    public static Customer create() {
        Customer customer = new Customer();
        customer.setIncome(BigDecimal.valueOf(3000));
        customer.setLastName("Doe");
        customer.setFirstName("John");
        customer.setEmail("john.doe@example.com");
        customer.setPhoneNumber("0705238260");
        customer.setCurrency("KES");
        customer.setActive(1);
        customer.setNationalId("12345");
        customer.setDateCreated(LocalDateTime.now());
        customer.setDateModified(LocalDateTime.now());

        return SpringContext.getBean(CustomerRepository.class).save(customer);
    }
    public static Customer create(Integer active) {
        Customer customer = new Customer();
        customer.setIncome(BigDecimal.valueOf(3000));
        customer.setLastName("Doe");
        customer.setFirstName("John");
        customer.setEmail("john.doe@example.com");
        customer.setPhoneNumber("0705238260");
        customer.setCurrency("KES");
        customer.setActive(active);
        customer.setNationalId("12345");
        customer.setDateCreated(LocalDateTime.now());
        customer.setDateModified(LocalDateTime.now());

        return SpringContext.getBean(CustomerRepository.class).save(customer);
    }

    public static void deleteAll() {
        SpringContext.getBean(CustomerRepository.class).deleteAll();
    }
}
