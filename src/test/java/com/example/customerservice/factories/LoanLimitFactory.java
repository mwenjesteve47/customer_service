package com.example.customerservice.factories;

import com.example.customerservice.model.Customer;
import com.example.customerservice.model.LoanLimit;
import com.example.customerservice.repository.CustomerRepository;
import com.example.customerservice.repository.LoanLimitRepository;
import com.example.customerservice.testHelpers.SpringContext;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LoanLimitFactory {
    public static LoanLimit create(Customer customer) {
        LoanLimit loanLimit = new LoanLimit();
        loanLimit.setCreditLimit(BigDecimal.valueOf(1000));
        loanLimit.setAvailableLimit(BigDecimal.valueOf(1000));
        loanLimit.setCurrency("KES");
        loanLimit.setCustomer(customer);
        return SpringContext.getBean(LoanLimitRepository.class).save(loanLimit);
    }

    public static void deleteAll() {
        SpringContext.getBean(LoanLimitRepository.class).deleteAll();
    }
}
