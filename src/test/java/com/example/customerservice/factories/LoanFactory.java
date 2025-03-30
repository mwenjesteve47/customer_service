package com.example.customerservice.factories;



import com.example.customerservice.enums.BillingCycleType;
import com.example.customerservice.enums.LoanState;
import com.example.customerservice.enums.LoanStructure;
import com.example.customerservice.model.Customer;
import com.example.customerservice.model.Loan;
import com.example.customerservice.model.LoanProduct;
import com.example.customerservice.repository.LoanRepository;
import com.example.customerservice.testHelpers.SpringContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LoanFactory {
    public static Loan create(Customer customer, LoanProduct loanProduct) {
        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setLoanProduct(loanProduct);
        loan.setState(LoanState.OPEN);
        loan.setLoanStructure(LoanStructure.INSTALLMENTS);
        loan.setBillingCycleType(BillingCycleType.INDIVIDUAL_DUE_DATE);
        loan.setCreatedAt(LocalDateTime.now());
        loan.setUpdatedAt(LocalDateTime.now());
        loan.setAmount(BigDecimal.valueOf(100));
        loan.setActive(1);
        loan.setAmountDue(BigDecimal.valueOf(1000));
        loan.setDueDate(LocalDate.now().plusDays(60));
        loan.setStartDate(LocalDate.now());

        return SpringContext.getBean(LoanRepository.class).save(loan);
    }

    public static void deleteAll() {
        SpringContext.getBean(LoanRepository.class).deleteAll();
    }
}
