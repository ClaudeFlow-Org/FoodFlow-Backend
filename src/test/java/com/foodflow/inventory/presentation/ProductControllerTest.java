package com.foodflow.inventory.presentation;

import com.foodflow.common.presentation.GlobalExceptionHandler;
import com.foodflow.inventory.application.InventoryCategoryResponse;
import com.foodflow.inventory.application.InventoryApplicationService;
import com.foodflow.inventory.application.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static com.foodflow.ControllerTestSupport.AUTHENTICATED_USER_ID;
import static com.foodflow.ControllerTestSupport.authenticatedUserArgumentResolver;
import static org.assertj.core.api.Assertions.assertThat;
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
class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private InventoryApplicationService inventoryApplicationService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ProductController(inventoryApplicationService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(authenticatedUserArgumentResolver())
                .build();
    }

    // Prueba integral: lista productos del usuario autenticado (BE-INT-004)
    @Test
    void listsProductsForAuthenticatedUser() throws Exception {
        ProductResponse response = ProductResponse.builder()
                .id(5L)
                .name("Tomate")
                .category("Vegetales")
                .supplier("Mercado")
                .stockLevel(new BigDecimal("20.00"))
                .unitCost(new BigDecimal("3.50"))
                .lowStockThreshold(new BigDecimal("5.00"))
                .unitOfMeasure("kg")
                .build();
        when(inventoryApplicationService.getAllProducts(AUTHENTICATED_USER_ID))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(5))
                .andExpect(jsonPath("$.data[0].name").value("Tomate"))
                .andExpect(jsonPath("$.data[0].category").value("Vegetales"));

        verify(inventoryApplicationService).getAllProducts(AUTHENTICATED_USER_ID);
    }
    // fin prueba

    // Prueba integral: crea producto de inventario con payload valido (BE-INT-005)
    @Test
    void createsProductWithValidPayload() throws Exception {
        ProductResponse response = ProductResponse.builder()
                .id(6L)
                .name("Harina")
                .category("Panaderia")
                .stockLevel(new BigDecimal("12.00"))
                .unitCost(new BigDecimal("2.00"))
                .lowStockThreshold(new BigDecimal("4.00"))
                .unitOfMeasure("kg")
                .build();
        when(inventoryApplicationService.addProduct(eq(AUTHENTICATED_USER_ID), any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Harina",
                                  "category": "Panaderia",
                                  "stockLevel": 12,
                                  "unitCost": 2,
                                  "lowStockThreshold": 4,
                                  "unitOfMeasure": "kg"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Product added successfully"))
                .andExpect(jsonPath("$.data.id").value(6))
                .andExpect(jsonPath("$.data.name").value("Harina"));

        ArgumentCaptor<com.foodflow.inventory.application.ProductRequest> requestCaptor =
                ArgumentCaptor.forClass(com.foodflow.inventory.application.ProductRequest.class);
        verify(inventoryApplicationService).addProduct(eq(AUTHENTICATED_USER_ID), requestCaptor.capture());
        assertThat(requestCaptor.getValue().getName()).isEqualTo("Harina");
        assertThat(requestCaptor.getValue().getStockLevel()).isEqualByComparingTo("12");
    }
    // fin prueba

    // Prueba integral: rechaza producto con stock y costo invalidos (BE-INT-006)
    @Test
    void rejectsInvalidProductPayload() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "stockLevel": -1,
                                  "unitCost": 0,
                                  "unitOfMeasure": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isArray());
    }
    // fin prueba

    // Prueba integral: actualiza producto existente con payload valido (BE-INT-023)
    @Test
    void updatesExistingProduct() throws Exception {
        ProductResponse response = ProductResponse.builder()
                .id(6L)
                .name("Harina Integral")
                .category("Panaderia")
                .stockLevel(new BigDecimal("18.00"))
                .unitCost(new BigDecimal("2.40"))
                .lowStockThreshold(new BigDecimal("5.00"))
                .unitOfMeasure("kg")
                .build();
        when(inventoryApplicationService.updateProduct(eq(AUTHENTICATED_USER_ID), eq(6L), any()))
                .thenReturn(response);

        mockMvc.perform(put("/api/products/6")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Harina Integral",
                                  "stockLevel": 18,
                                  "unitCost": 2.4,
                                  "lowStockThreshold": 5,
                                  "unitOfMeasure": "kg"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Product updated successfully"))
                .andExpect(jsonPath("$.data.name").value("Harina Integral"));

        verify(inventoryApplicationService).updateProduct(eq(AUTHENTICATED_USER_ID), eq(6L), any());
    }
    // fin prueba

    // Prueba integral: asigna categoria a producto existente (BE-INT-024)
    @Test
    void updatesProductCategory() throws Exception {
        ProductResponse response = ProductResponse.builder()
                .id(6L)
                .name("Harina")
                .category("Panaderia")
                .stockLevel(new BigDecimal("12.00"))
                .unitCost(new BigDecimal("2.00"))
                .lowStockThreshold(new BigDecimal("4.00"))
                .unitOfMeasure("kg")
                .build();
        when(inventoryApplicationService.updateProductCategory(eq(AUTHENTICATED_USER_ID), eq(6L), any()))
                .thenReturn(response);

        mockMvc.perform(put("/api/products/6/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Panaderia"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Product category updated successfully"))
                .andExpect(jsonPath("$.data.category").value("Panaderia"));

        verify(inventoryApplicationService).updateProductCategory(eq(AUTHENTICATED_USER_ID), eq(6L), any());
    }
    // fin prueba

    // Prueba integral: administra categorias de inventario (BE-INT-025)
    @Test
    void managesInventoryCategories() throws Exception {
        InventoryCategoryResponse category = InventoryCategoryResponse.builder()
                .id(30L)
                .name("Bebidas")
                .value("Bebidas")
                .label("Bebidas")
                .build();
        when(inventoryApplicationService.getCategories(AUTHENTICATED_USER_ID))
                .thenReturn(List.of(category));
        when(inventoryApplicationService.createCategory(eq(AUTHENTICATED_USER_ID), any()))
                .thenReturn(category);
        when(inventoryApplicationService.updateCategory(eq(AUTHENTICATED_USER_ID), eq(30L), any()))
                .thenReturn(InventoryCategoryResponse.builder()
                        .id(30L)
                        .name("Bebidas frias")
                        .value("Bebidas frias")
                        .label("Bebidas frias")
                        .build());

        mockMvc.perform(get("/api/products/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Bebidas"));

        mockMvc.perform(post("/api/products/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Bebidas"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Category created successfully"))
                .andExpect(jsonPath("$.data.id").value(30));

        mockMvc.perform(put("/api/products/categories/30")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Bebidas frias"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Category updated successfully"))
                .andExpect(jsonPath("$.data.name").value("Bebidas frias"));

        mockMvc.perform(delete("/api/products/categories/30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Category deleted successfully"));

        verify(inventoryApplicationService).getCategories(AUTHENTICATED_USER_ID);
        verify(inventoryApplicationService).createCategory(eq(AUTHENTICATED_USER_ID), any());
        verify(inventoryApplicationService).updateCategory(eq(AUTHENTICATED_USER_ID), eq(30L), any());
        verify(inventoryApplicationService).deleteCategory(AUTHENTICATED_USER_ID, 30L);
    }
    // fin prueba

    // Prueba integral: elimina producto existente (BE-INT-026)
    @Test
    void deletesProduct() throws Exception {
        mockMvc.perform(delete("/api/products/6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Product deleted successfully"));

        verify(inventoryApplicationService).deleteProduct(AUTHENTICATED_USER_ID, 6L);
    }
    // fin prueba
}
