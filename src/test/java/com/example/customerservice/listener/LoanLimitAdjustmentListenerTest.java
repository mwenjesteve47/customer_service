package com.example.customerservice.listener;

import com.example.customerservice.AbstractIntegrationTest;
import com.example.customerservice.config.AppConfig;
import com.example.customerservice.dto.LoanLimitAdjustmentEventDto;
import com.example.customerservice.factories.CustomerFactory;
import com.example.customerservice.factories.LoanFactory;
import com.example.customerservice.factories.LoanLimitFactory;
import com.example.customerservice.factories.LoanProductFactory;
import com.example.customerservice.model.LoanLimit;
import com.example.customerservice.repository.LoanLimitRepository;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
@Slf4j
public class LoanLimitAdjustmentListenerTest extends AbstractIntegrationTest {

    @Autowired
    RabbitTemplate defaultRabbitTemplate;

    @Autowired
    AppConfig applicationProperties;

    @Autowired
    LoanLimitRepository loanLimitRepository;

    @Test
    void givenValidLoanAdjustmentRequest_whenhandleLoanLimitReduction_thenProcessLoanLimitReductionEventSuccessfully(CapturedOutput capturedOutput) throws Exception {
        var customer = CustomerFactory.create();
        LoanLimitFactory.create(customer);
        var loanProduct = LoanProductFactory.create();
        var loan = LoanFactory.create(customer,loanProduct);

        var loanLimitAdjustmentEventDto = LoanLimitAdjustmentEventDto.builder()
                                        .adjustment("subtraction")
                                        .loanId(loan.getId())
                                        .customerId(customer.getId())
                                        .build();

        var request = """
                {
                  "adjustment": "%s",
                  "loanId": %s,
                  "customerId": %s
                }
                """.formatted( loanLimitAdjustmentEventDto.getAdjustment(),
                loanLimitAdjustmentEventDto.getLoanId(),loanLimitAdjustmentEventDto.getCustomerId());
        log.info("request: {}", request);
        defaultRabbitTemplate.convertAndSend(applicationProperties.getExchange(), applicationProperties.getRoutingKey(), request);

        // Wait for message to be processed
        Awaitility
                .await()
                .atMost(Duration.ofSeconds(5))
                .until(() -> {
                    Optional<LoanLimit> updatedLoanLimit = loanLimitRepository.findByCustomerId(loanLimitAdjustmentEventDto.getCustomerId());
                    return updatedLoanLimit.isPresent() &&
                            updatedLoanLimit.get().getAvailableLimit().compareTo(new BigDecimal("1000.00")) < 0;
                });
        // Fetch updated loan limit and validate
        LoanLimit updatedLimit = loanLimitRepository.findByCustomerId(loanLimitAdjustmentEventDto.getCustomerId()).orElseThrow();
        assertThat(updatedLimit.getAvailableLimit()).isLessThan(new BigDecimal("1000.00"));

    }

    @Test
    void givenValidLoanAdjustmentRequest_whenhandleLoanLimitIncrement_thenProcessLoanLimitIncrementEventSuccessfully(CapturedOutput capturedOutput) throws Exception {
        var customer = CustomerFactory.create();
        LoanLimitFactory.create(customer);
        var loanProduct = LoanProductFactory.create();
        var loan = LoanFactory.create(customer,loanProduct);

        var loanLimitAdjustmentEventDto = LoanLimitAdjustmentEventDto.builder()
                .adjustment("addition")
                .loanId(loan.getId())
                .customerId(customer.getId())
                .build();

        var request = """
                {
                  "adjustment": "%s",
                  "loanId": %s,
                  "customerId": %s
                }
                """.formatted( loanLimitAdjustmentEventDto.getAdjustment(),
                loanLimitAdjustmentEventDto.getLoanId(),loanLimitAdjustmentEventDto.getCustomerId());
        log.info("request: {}", request);
        defaultRabbitTemplate.convertAndSend(applicationProperties.getExchange(), applicationProperties.getRoutingKey(), request);

        // Wait for message to be processed
        Awaitility
                .await()
                .atMost(Duration.ofSeconds(5))
                .until(() -> {
                    Optional<LoanLimit> updatedLoanLimit = loanLimitRepository.findByCustomerId(loanLimitAdjustmentEventDto.getCustomerId());
                    return updatedLoanLimit.isPresent() &&
                            updatedLoanLimit.get().getAvailableLimit().compareTo(new BigDecimal("1000.00")) > 0;
                });

        // Fetch updated loan limit and validate
        LoanLimit updatedLimit = loanLimitRepository.findByCustomerId(loanLimitAdjustmentEventDto.getCustomerId()).orElseThrow();
        assertThat(updatedLimit.getAvailableLimit()).isGreaterThan(new BigDecimal("1000.00"));

    }

}
