package com.example.customerservice.dto;

import lombok.*;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanLimitsDto {
    private BigDecimal creditLimit;
    private BigDecimal availableLimit;
    private String currency;
}
