package com.example.customerservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerRequest {
    @NotBlank(message = "firstName cannot be blank")
    private String firstName;

    @NotBlank(message = "lastName cannot be blank")
    private String lastName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^(\\+?\\d{1,3})?\\d{10}$", message = "Invalid phone number format")
    private String phoneNumber;
    @NotBlank(message = "Currency cannot be empty")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid 3-letter ISO code (e.g., USD, KES)")
    private String currency;
    @NotBlank(message = "national Id number cannot be blank")
    @Pattern(regexp = "^\\d{6,10}$", message = "National ID must be between 6 and 10 digits")
    private String nationalId;
    @NotNull(message = "income cannot be null")
    @DecimalMin(value = "500", message = "Income must be at least 500")
    private BigDecimal income;
    private CustomerNotificationPreferencesDto customerNotificationPreferencesDto;
}
