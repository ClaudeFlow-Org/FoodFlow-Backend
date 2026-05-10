package com.foodflow.catalog.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DishDomainTest {

    // Prueba unitaria: actualiza datos validos de plato (BE-UT-011)
    @Test
    void updatesDishDetailsWithValidValues() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 5, 8, 10, 0);
        Dish dish = Dish.builder()
                .name("Menu del dia")
                .description("Entrada y fondo")
                .price(new BigDecimal("18.00"))
                .ingredients("arroz, pollo")
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();

        dish.updateDetails("Menu ejecutivo", "Incluye bebida", new BigDecimal("22.00"), "arroz, pollo, refresco");

        assertThat(dish.getName()).isEqualTo("Menu ejecutivo");
        assertThat(dish.getDescription()).isEqualTo("Incluye bebida");
        assertThat(dish.getPrice()).isEqualByComparingTo("22.00");
        assertThat(dish.getIngredients()).isEqualTo("arroz, pollo, refresco");
        assertThat(dish.getUpdatedAt()).isAfter(createdAt);
    }
    // fin prueba

    // Prueba unitaria: conserva nombre y precio ante valores invalidos (BE-UT-012)
    @Test
    void ignoresBlankNameAndNonPositivePrice() {
        Dish dish = Dish.builder()
                .name("Causa")
                .price(new BigDecimal("12.00"))
                .build();

        dish.updateDetails("   ", null, BigDecimal.ZERO, null);

        assertThat(dish.getName()).isEqualTo("Causa");
        assertThat(dish.getPrice()).isEqualByComparingTo("12.00");
    }
    // fin prueba
}
