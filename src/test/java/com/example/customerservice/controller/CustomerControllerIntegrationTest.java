package com.example.customerservice.controller;

import com.example.customerservice.AbstractIntegrationTest;
import com.example.customerservice.config.AppConfig;
import com.example.customerservice.dto.CustomerNotificationPreferencesDto;
import com.example.customerservice.dto.CustomerRequest;
import com.example.customerservice.dto.LoanLimitsDto;
import com.example.customerservice.factories.CustomerFactory;
import com.example.customerservice.factories.CustomerNotificationPreferencesFactory;
import com.example.customerservice.factories.LoanLimitFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

import static com.example.customerservice.testHelpers.RabbitMqTestHelper.createLoanLimitQueue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class CustomerControllerIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    AppConfig properties;

    @BeforeEach
    void preCleanUp() {
        CustomerNotificationPreferencesFactory.deleteAll();
        LoanLimitFactory.deleteAll();
        CustomerFactory.deleteAll();
    }

    @Test
    void givenValidCustomerRequest_whenCreatingCustomer_thenReturnSuccess() throws Exception {

        createLoanLimitQueue(properties, rabbitTemplate);
        // Given
        var request = CustomerRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .currency("KES")
                .income(BigDecimal.valueOf(1000))
                .nationalId("4234159")
                .phoneNumber("+254712345671")
                .customerNotificationPreferencesDto(CustomerNotificationPreferencesDto.builder()
                        .pushNotificationEnabled(Boolean.TRUE)
                        .smsEnabled(Boolean.TRUE)
                        .emailEnabled(Boolean.TRUE)
                        .build()
                )
                .build();


        // When & Then
        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Customer created successfully"))
                .andExpect(jsonPath("$.data.firstName").value("John"));
    }


    @Test
    void givenValidLoanLimitRequest_whenCreatingLoanLimit_thenReturnSuccess() throws Exception {
        // Given
        var customer = CustomerFactory.create();
        LoanLimitFactory.create(customer);
        var request = LoanLimitsDto.builder().creditLimit(BigDecimal.valueOf(1000))
                .availableLimit(BigDecimal.valueOf(100))
                .currency(customer.getCurrency())
                .build();

        // When & Then
        mockMvc.perform(post("/customer/"+customer.getId()+"/set-loan-limit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Loan limit set successfully"));
    }

    @Test
    void givenInValidCustomerIDForLoanLimitRequest_whenCreatingLoanLimit_thenReturnLoanLimitNotFoundForCustomer() throws Exception {
        // Given
        var customer = CustomerFactory.create();
        LoanLimitFactory.create(customer);
        var request = LoanLimitsDto.builder().creditLimit(BigDecimal.valueOf(1000))
                .availableLimit(BigDecimal.valueOf(100))
                .currency(customer.getCurrency())
                .build();

        var randomCustomerID = Faker.instance().random().nextInt(1000);

        // When & Then
        mockMvc.perform(post("/customer/"+randomCustomerID+"/set-loan-limit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Loan limit not found for customer"));
    }

    @Test
    void givenValidFilterRequest_whenFetchingCustomer_thenReturnCustomerSuccessfully() throws Exception {
        // Given: Some loan products exist
        var customer = CustomerFactory.create();
        LoanLimitFactory.create(customer);
        CustomerNotificationPreferencesFactory.create(customer);

        mockMvc.perform(MockMvcRequestBuilders.get("/customer")
                        .param("page", "1")
                        .param("size", "10")
                        .param("nationalId", "12345")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Customer retrieved"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.data.content.[0].nationalId").value("12345"))
                .andExpect(jsonPath("$.data.data.content.[0].lastName").value("Doe"));
    }


    @Test
    void givenExistentCustomerId_whenDeactivatingCustomer_thenReturnSuccess() throws Exception {
        var customer = CustomerFactory.create();

        mockMvc.perform(put("/customer/"+customer.getId()+"/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    @Test
    void givenNonExistentCustomerId_whenDeactivatingCustomer_thenReturnNotFound() throws Exception {
        // Given: A non-existent customer ID
        var nonExistentLoanProductId = Faker.instance().random().nextLong(1000);

        // When: Sending the request for a non-existent customer
        mockMvc.perform(put("/customer/" + nonExistentLoanProductId + "/deactivate"))
                // Then: Expect a 404 Not Found response
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("customer with id:  "+nonExistentLoanProductId  +" not found"));
    }
    @Test
    void givenAlreadyDeactivatedCustomer_whenDeactivatingCustomer_thenReturnBadRequest() throws Exception {
        // Given: A loan product that is already deactivated
        var customer = CustomerFactory.create(0);

        // When: Sending the request to deactivate an already deactivated loan product
        mockMvc.perform(put("/customer/" + customer.getId() + "/deactivate"))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Customer is already inactive."));
    }
}
