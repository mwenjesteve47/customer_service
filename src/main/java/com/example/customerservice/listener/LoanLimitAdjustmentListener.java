package com.example.customerservice.listener;

import com.example.customerservice.helper.LoanLimitHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanLimitAdjustmentListener {
    private final LoanLimitHelper loanLimitHelper;

    @RabbitListener(queues = "loan.limit.queue")
    public void handleLoanLatenessEvent(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode eventData = objectMapper.readTree(message);
            log.info("Received message to adjust loan limit: {}", eventData);

            Long customerId = eventData.get("customerId").asLong();
            String adjustment = eventData.get("adjustment").asText();

            if(adjustment.equalsIgnoreCase("subtraction")){
                // Reduce loan limit based on the configured percentage
                loanLimitHelper.adjustLoanLimit(customerId,false);
            }else if(adjustment.equalsIgnoreCase("addition")){
                // Increase loan limit based on the configured percentage
                loanLimitHelper.adjustLoanLimit(customerId,true);
            }
        } catch (Exception e) {
            log.error("Error processing loan adjustment event: " + e.getMessage());
        }
    }
}
