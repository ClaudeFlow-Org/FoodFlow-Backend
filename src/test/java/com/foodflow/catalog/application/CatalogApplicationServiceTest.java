package com.foodflow.catalog.application;

import com.foodflow.catalog.domain.Dish;
import com.foodflow.catalog.domain.DishRepository;
import com.foodflow.common.domain.DuplicateResourceException;
import com.foodflow.common.domain.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogApplicationServiceTest {

    @Mock
    private DishRepository dishRepository;

    // Prueba unitaria: crea plato y persiste usuario propietario (BE-UT-022)
    @Test
    void addsDishForUser() {
        CatalogApplicationService service = new CatalogApplicationService(dishRepository);
        when(dishRepository.existsByUserIdAndName(77L, "Ceviche")).thenReturn(false);
        when(dishRepository.save(any(Dish.class))).thenAnswer(invocation -> {
            Dish dish = invocation.getArgument(0);
            dish.setId(Dish.DishId.of(10L));
            dish.setCreatedAt(LocalDateTime.of(2026, 5, 9, 10, 0));
            return dish;
        });

        DishResponse response = service.addDish(77L, DishRequest.builder()
                .name("Ceviche")
                .description("Clasico")
                .price(new BigDecimal("32.00"))
                .ingredients("pescado, limon")
                .build());

        ArgumentCaptor<Dish> dishCaptor = ArgumentCaptor.forClass(Dish.class);
        verify(dishRepository).save(dishCaptor.capture());
        assertThat(dishCaptor.getValue().getUserId()).isEqualTo(77L);
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getName()).isEqualTo("Ceviche");
    }
    // fin prueba

    // Prueba unitaria: rechaza plato duplicado para el usuario (BE-UT-023)
    @Test
    void rejectsDuplicateDishName() {
        CatalogApplicationService service = new CatalogApplicationService(dishRepository);
        when(dishRepository.existsByUserIdAndName(77L, "Ceviche")).thenReturn(true);

        assertThatThrownBy(() -> service.addDish(77L, DishRequest.builder()
                .name("Ceviche")
                .price(new BigDecimal("32.00"))
                .build()))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Dish already exists: name Ceviche");
    }
    // fin prueba

    // Prueba unitaria: rechaza precio nulo o no positivo (BE-UT-024)
    @Test
    void rejectsInvalidPrice() {
        CatalogApplicationService service = new CatalogApplicationService(dishRepository);

        assertThatThrownBy(() -> service.addDish(77L, DishRequest.builder()
                .name("Ceviche")
                .price(BigDecimal.ZERO)
                .build()))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("price: Price must be greater than 0");
    }
    // fin prueba

    // Prueba unitaria: busca platos por nombre cuando hay termino (BE-UT-025)
    @Test
    void searchesDishesByName() {
        CatalogApplicationService service = new CatalogApplicationService(dishRepository);
        Dish dish = Dish.builder()
                .id(Dish.DishId.of(11L))
                .name("Tallarines verdes")
                .price(new BigDecimal("24.00"))
                .userId(77L)
                .createdAt(LocalDateTime.of(2026, 5, 9, 11, 0))
                .build();
        when(dishRepository.findByUserIdAndNameContaining(77L, "verde"))
                .thenReturn(List.of(dish));

        List<DishResponse> results = service.searchDishes(77L, "verde");

        assertThat(results).extracting(DishResponse::getName).containsExactly("Tallarines verdes");
        verify(dishRepository).findByUserIdAndNameContaining(77L, "verde");
    }
    // fin prueba

    // Prueba unitaria: impide leer plato de otro usuario (BE-UT-026)
    @Test
    void rejectsAccessToAnotherUsersDish() {
        CatalogApplicationService service = new CatalogApplicationService(dishRepository);
        Dish dish = Dish.builder()
                .id(Dish.DishId.of(11L))
                .name("Ají de gallina")
                .price(new BigDecimal("22.00"))
                .userId(88L)
                .build();
        when(dishRepository.findById(Dish.DishId.of(11L))).thenReturn(Optional.of(dish));

        assertThatThrownBy(() -> service.getDishById(77L, 11L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("You do not have access to this dish");
    }
    // fin prueba
}
