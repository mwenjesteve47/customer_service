package com.example.customerservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerNotificationPreferencesDto {
    @JsonProperty("smsNotifications")
    @NotNull
    private boolean smsEnabled;
    @JsonProperty("emailNotifications")
    @NotNull
    private boolean emailEnabled;
    @JsonProperty("pushNotifications")
    @NotNull
    private boolean pushNotificationEnabled;
}
