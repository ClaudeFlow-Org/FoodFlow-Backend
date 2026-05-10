package com.foodflow.inventory.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ProductDomainTest {

    // Prueba unitaria: actualiza producto con valores validos de inventario (BE-UT-013)
    @Test
    void updatesProductDetailsWithValidInventoryValues() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 5, 8, 9, 0);
        Product product = Product.builder()
                .name("Tomate")
                .description("Fresco")
                .category("Vegetales")
                .supplier("Mercado")
                .stockLevel(new BigDecimal("10.00"))
                .unitCost(new BigDecimal("3.00"))
                .lowStockThreshold(new BigDecimal("4.00"))
                .unitOfMeasure("kg")
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();

        product.updateDetails(
                "Tomate italiano",
                "Seleccionado",
                "Verduras",
                "Proveedor A",
                new BigDecimal("16.00"),
                new BigDecimal("3.50"),
                new BigDecimal("5.00"),
                "kg"
        );

        assertThat(product.getName()).isEqualTo("Tomate italiano");
        assertThat(product.getCategory()).isEqualTo("Verduras");
        assertThat(product.getStockLevel()).isEqualByComparingTo("16.00");
        assertThat(product.getUnitCost()).isEqualByComparingTo("3.50");
        assertThat(product.getLowStockThreshold()).isEqualByComparingTo("5.00");
        assertThat(product.getUpdatedAt()).isAfter(createdAt);
    }
    // fin prueba

    // Prueba unitaria: ignora valores negativos al actualizar inventario (BE-UT-014)
    @Test
    void ignoresNegativeInventoryValues() {
        Product product = Product.builder()
                .name("Arroz")
                .stockLevel(new BigDecimal("20.00"))
                .unitCost(new BigDecimal("2.50"))
                .lowStockThreshold(new BigDecimal("6.00"))
                .unitOfMeasure("kg")
                .build();

        product.updateDetails(
                " ",
                null,
                null,
                null,
                new BigDecimal("-1.00"),
                new BigDecimal("-2.00"),
                new BigDecimal("-3.00"),
                " "
        );

        assertThat(product.getName()).isEqualTo("Arroz");
        assertThat(product.getStockLevel()).isEqualByComparingTo("20.00");
        assertThat(product.getUnitCost()).isEqualByComparingTo("2.50");
        assertThat(product.getLowStockThreshold()).isEqualByComparingTo("6.00");
        assertThat(product.getUnitOfMeasure()).isEqualTo("kg");
    }
    // fin prueba
}
