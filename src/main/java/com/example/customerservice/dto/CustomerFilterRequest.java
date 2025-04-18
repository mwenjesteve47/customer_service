package com.example.customerservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerFilterRequest {
    private String phoneNumber;
    private String nationalId;
    private Integer active;
    private Integer id;

    @Builder.Default
    @NotNull(message = "Page number is not supposed to be empty.")
    @Min(1)
    private int page = 1;

    @Builder.Default
    @NotNull(message = "Limit is not supposed to be empty.")
    @Min(5)
    private int size = 10;
}
