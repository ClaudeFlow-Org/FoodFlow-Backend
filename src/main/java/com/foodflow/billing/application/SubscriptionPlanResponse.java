package com.foodflow.billing.application;

import com.foodflow.billing.domain.SubscriptionPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanResponse {

    private String name;
    private Double monthlyPrice;
    private List<String> benefits;
}
