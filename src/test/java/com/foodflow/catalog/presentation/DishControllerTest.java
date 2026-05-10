package com.foodflow.catalog.presentation;

import com.foodflow.catalog.application.CatalogApplicationService;
import com.foodflow.catalog.application.DishResponse;
import com.foodflow.common.presentation.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.foodflow.ControllerTestSupport.AUTHENTICATED_USER_ID;
import static com.foodflow.ControllerTestSupport.authenticatedUserArgumentResolver;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DishControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CatalogApplicationService catalogApplicationService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new DishController(catalogApplicationService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(authenticatedUserArgumentResolver())
                .build();
    }

    // Prueba integral: crea plato con usuario autenticado (BE-INT-010)
    @Test
    void createsDishForAuthenticatedUser() throws Exception {
        DishResponse response = DishResponse.builder()
                .id(22L)
                .name("Ceviche")
                .price(new BigDecimal("32.00"))
                .ingredients("pescado, limon")
                .createdAt(LocalDateTime.of(2026, 5, 9, 10, 0))
                .build();
        when(catalogApplicationService.addDish(eq(AUTHENTICATED_USER_ID), any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Ceviche",
                                  "description": "Clasico",
                                  "price": 32,
                                  "ingredients": "pescado, limon"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Dish added successfully"))
                .andExpect(jsonPath("$.data.id").value(22))
                .andExpect(jsonPath("$.data.name").value("Ceviche"));

        verify(catalogApplicationService).addDish(eq(AUTHENTICATED_USER_ID), any());
    }
    // fin prueba

    // Prueba integral: busca platos por query string (BE-INT-011)
    @Test
    void searchesDishesWithQueryParameter() throws Exception {
        when(catalogApplicationService.searchDishes(AUTHENTICATED_USER_ID, "cev"))
                .thenReturn(List.of(DishResponse.builder()
                        .id(22L)
                        .name("Ceviche")
                        .price(new BigDecimal("32.00"))
                        .createdAt(LocalDateTime.of(2026, 5, 9, 10, 0))
                        .build()));

        mockMvc.perform(get("/api/dishes").param("search", "cev"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Ceviche"));

        verify(catalogApplicationService).searchDishes(AUTHENTICATED_USER_ID, "cev");
    }
    // fin prueba

    // Prueba integral: rechaza plato con nombre corto y precio invalido (BE-INT-012)
    @Test
    void rejectsInvalidDishPayload() throws Exception {
        mockMvc.perform(post("/api/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "A",
                                  "price": 0,
                                  "ingredients": "x"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isArray());
    }
    // fin prueba

    // Prueba integral: obtiene plato por identificador (BE-INT-027)
    @Test
    void getsDishById() throws Exception {
        when(catalogApplicationService.getDishById(AUTHENTICATED_USER_ID, 22L))
                .thenReturn(DishResponse.builder()
                        .id(22L)
                        .name("Ceviche")
                        .description("Clasico")
                        .price(new BigDecimal("32.00"))
                        .ingredients("pescado, limon")
                        .createdAt(LocalDateTime.of(2026, 5, 9, 10, 0))
                        .build());

        mockMvc.perform(get("/api/dishes/22"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(22))
                .andExpect(jsonPath("$.data.description").value("Clasico"));

        verify(catalogApplicationService).getDishById(AUTHENTICATED_USER_ID, 22L);
    }
    // fin prueba

    // Prueba integral: actualiza plato existente (BE-INT-028)
    @Test
    void updatesDish() throws Exception {
        when(catalogApplicationService.updateDish(eq(AUTHENTICATED_USER_ID), eq(22L), any()))
                .thenReturn(DishResponse.builder()
                        .id(22L)
                        .name("Ceviche Mixto")
                        .price(new BigDecimal("38.00"))
                        .ingredients("pescado, mariscos, limon")
                        .createdAt(LocalDateTime.of(2026, 5, 9, 10, 0))
                        .build());

        mockMvc.perform(put("/api/dishes/22")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Ceviche Mixto",
                                  "description": "Especial",
                                  "price": 38,
                                  "ingredients": "pescado, mariscos, limon"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Dish updated successfully"))
                .andExpect(jsonPath("$.data.name").value("Ceviche Mixto"));

        verify(catalogApplicationService).updateDish(eq(AUTHENTICATED_USER_ID), eq(22L), any());
    }
    // fin prueba

    // Prueba integral: elimina plato existente (BE-INT-029)
    @Test
    void deletesDish() throws Exception {
        mockMvc.perform(delete("/api/dishes/22"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Dish deleted successfully"));

        verify(catalogApplicationService).deleteDish(AUTHENTICATED_USER_ID, 22L);
    }
    // fin prueba
}
