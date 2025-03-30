package com.example.customerservice.controller;

import com.example.customerservice.dto.*;
import com.example.customerservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponseDto>> createCustomer(@Valid @RequestBody CustomerRequest customer) {
        var savedCustomer = customerService.createCustomer(customer);
        return ResponseEntity.ok(new ApiResponse<>(true,"Customer created successfully", savedCustomer));
    }

    @GetMapping
    public ResponseEntity<?> getCustomer(@Valid CustomerFilterRequest request) {
        var customer = customerService.getCustomers(request);
        return ResponseEntity.ok(new ApiResponse<>(true,"Customer retrieved", customer));
    }



    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateCustomer(@PathVariable long id) {
        var response = customerService.deactivateCustomer(id);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/{customerId}/set-loan-limit")
    public ResponseEntity<ApiResponse<?>> setLoanLimit(@PathVariable Long customerId, @RequestBody LoanLimitsDto loanLimitsDto) {
        customerService.setLoanLimit(customerId, loanLimitsDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Loan limit set successfully"));
    }
}
