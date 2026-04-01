package com.foodflow.billing.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscribeRequest {

    @NotBlank(message = "Plan is required")
    @Pattern(regexp = "FREE|STANDARD|PREMIUM", message = "Plan must be FREE, STANDARD, or PREMIUM")
    private String plan;
}
