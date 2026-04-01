package com.foodflow.sales.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    @NotBlank(message = "Table identifier is required")
    @Size(max = 50, message = "Table identifier must not exceed 50 characters")
    private String tableIdentifier;

    @NotEmpty(message = "At least one line item is required")
    private List<OrderLineItemRequest> lineItems;
}
