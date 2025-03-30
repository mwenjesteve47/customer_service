package com.example.customerservice.helper;

import com.example.customerservice.config.AppConfig;
import com.example.customerservice.repository.CustomerRepository;
import com.example.customerservice.repository.LoanLimitRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Service
@Slf4j
@RequiredArgsConstructor
public class LoanLimitHelper {
    private final CustomerRepository customerRepository;
    private final AppConfig appConfig;
    private final LoanLimitRepository loanLimitRepository;


    @Transactional
    public void adjustLoanLimit(Long customerId, boolean isIncrease) {
        var loanLimit = loanLimitRepository.findByCustomerId(customerId);

        if (loanLimit.isEmpty()) {
            log.warn("No loan limit set for customer {}", customerId);
            return;
        }

        BigDecimal currentLimit = loanLimit.get().getAvailableLimit();

        // Block reduction if the loan limit is already at zero
        if (!isIncrease && (currentLimit == null || currentLimit.compareTo(BigDecimal.ZERO) <= 0)) {
            log.warn("Loan limit is already at minimum for customer: {}", customerId);
            return;
        }

        // Default to a minimum starting point if increasing and the current limit is null
        if (isIncrease && (currentLimit == null)) {
            currentLimit = BigDecimal.ZERO;
        }

        // Calculate adjustment amount
        BigDecimal adjustmentAmount = currentLimit.multiply(BigDecimal.valueOf(appConfig.getAdjustmentPercentage()))
                .divide(BigDecimal.valueOf(100));

        // Apply increase or decrease
        BigDecimal newLoanLimit = isIncrease ? currentLimit.add(adjustmentAmount)
                : currentLimit.subtract(adjustmentAmount);

        // Ensure the loan limit does not go below zero when reducing
        if (!isIncrease && newLoanLimit.compareTo(BigDecimal.ZERO) < 0) {
            newLoanLimit = BigDecimal.ZERO;
        }

        // Update loan limit
        var fetchedLoan = loanLimit.get();
        fetchedLoan.setAvailableLimit(newLoanLimit);

        loanLimitRepository.save(fetchedLoan);

        log.info("Loan limit {} for customer {}. New Limit: {}", isIncrease ? "increased" : "reduced", customerId, newLoanLimit);
    }


}
