package com.foodflow.sales.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class OrderRequest {

    @NotBlank(message = "Table identifier is required")
    @Size(max = 50, message = "Table identifier must not exceed 50 characters")
    private String tableIdentifier;

    @NotEmpty(message = "At least one line item is required")
    @Size(max = 30, message = "Order must not exceed 30 line items")
    private List<OrderLineItemRequest> lineItems;

    public OrderRequest() {
    }

    public OrderRequest(String tableIdentifier, List<OrderLineItemRequest> lineItems) {
        this.tableIdentifier = tableIdentifier;
        this.lineItems = lineItems;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getTableIdentifier() {
        return tableIdentifier;
    }

    public void setTableIdentifier(String tableIdentifier) {
        this.tableIdentifier = tableIdentifier;
    }

    public List<OrderLineItemRequest> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<OrderLineItemRequest> lineItems) {
        this.lineItems = lineItems;
    }

    public static class Builder {
        private String tableIdentifier;
        private List<OrderLineItemRequest> lineItems;

        public Builder tableIdentifier(String tableIdentifier) {
            this.tableIdentifier = tableIdentifier;
            return this;
        }

        public Builder lineItems(List<OrderLineItemRequest> lineItems) {
            this.lineItems = lineItems;
            return this;
        }

        public OrderRequest build() {
            return new OrderRequest(tableIdentifier, lineItems);
        }
    }
}
