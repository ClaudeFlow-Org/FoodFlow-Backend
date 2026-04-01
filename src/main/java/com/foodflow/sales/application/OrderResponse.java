package com.foodflow.sales.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long id;
    private String tableIdentifier;
    private LocalDateTime orderDate;
    private List<OrderLineItemResponse> lineItems;
    private BigDecimal totalAmount;
}
