package com.foodflow.sales.presentation;

import com.foodflow.common.presentation.GlobalExceptionHandler;
import com.foodflow.sales.application.OrderResponse;
import com.foodflow.sales.application.SalesApplicationService;
import com.foodflow.sales.domain.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SalesApplicationService salesApplicationService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new OrderController(salesApplicationService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(authenticatedUserArgumentResolver())
                .build();
    }

    // Prueba integral: crea orden desde contrato HTTP y usuario autenticado (BE-INT-001)
    @Test
    void createsOrderForAuthenticatedUser() throws Exception {
        OrderResponse response = OrderResponse.builder()
                .id(9L)
                .orderNumber("77-001")
                .tableIdentifier("Mesa 4")
                .lineItems(List.of())
                .totalAmount(new BigDecimal("45.50"))
                .status(Order.OrderStatus.PENDIENTE)
                .build();
        when(salesApplicationService.createOrder(eq(AUTHENTICATED_USER_ID), any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "tableIdentifier": "Mesa 4",
                                  "lineItems": [
                                    {
                                      "dishId": 1,
                                      "dishName": "Menu",
                                      "unitPrice": 22.75,
                                      "quantity": 2
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Order created successfully"))
                .andExpect(jsonPath("$.data.orderNumber").value("77-001"))
                .andExpect(jsonPath("$.data.totalAmount").value(45.50));

        ArgumentCaptor<com.foodflow.sales.application.OrderRequest> requestCaptor =
                ArgumentCaptor.forClass(com.foodflow.sales.application.OrderRequest.class);
        verify(salesApplicationService).createOrder(eq(AUTHENTICATED_USER_ID), requestCaptor.capture());
        assertThat(requestCaptor.getValue().getTableIdentifier()).isEqualTo("Mesa 4");
        assertThat(requestCaptor.getValue().getLineItems()).hasSize(1);
    }
    // fin prueba

    // Prueba integral: rechaza orden sin mesa ni items (BE-INT-002)
    @Test
    void rejectsInvalidOrderPayload() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "tableIdentifier": "",
                                  "lineItems": []
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isArray());
    }
    // fin prueba

    // Prueba integral: normaliza estado entregado al actualizar orden (BE-INT-003)
    @Test
    void updatesOrderStatusUsingExternalStatusAlias() throws Exception {
        OrderResponse response = OrderResponse.builder()
                .id(9L)
                .orderNumber("77-001")
                .tableIdentifier("Mesa 4")
                .lineItems(List.of())
                .totalAmount(new BigDecimal("45.50"))
                .status(Order.OrderStatus.ENTREGADA)
                .build();
        when(salesApplicationService.updateOrderStatus(
                AUTHENTICATED_USER_ID,
                9L,
                Order.OrderStatus.ENTREGADA
        )).thenReturn(response);

        mockMvc.perform(put("/api/orders/9/status")
                        .param("status", "DELIVERED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("ENTREGADA"));

        verify(salesApplicationService).updateOrderStatus(
                AUTHENTICATED_USER_ID,
                9L,
                Order.OrderStatus.ENTREGADA
        );
    }
    // fin prueba

    // Prueba integral: lista ordenes del usuario autenticado (BE-INT-030)
    @Test
    void listsOrdersForAuthenticatedUser() throws Exception {
        when(salesApplicationService.getAllOrders(AUTHENTICATED_USER_ID))
                .thenReturn(List.of(OrderResponse.builder()
                        .id(9L)
                        .orderNumber("77-001")
                        .tableIdentifier("Mesa 4")
                        .lineItems(List.of())
                        .totalAmount(new BigDecimal("45.50"))
                        .status(Order.OrderStatus.PENDIENTE)
                        .build()));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].orderNumber").value("77-001"))
                .andExpect(jsonPath("$.data[0].status").value("PENDIENTE"));

        verify(salesApplicationService).getAllOrders(AUTHENTICATED_USER_ID);
    }
    // fin prueba

    // Prueba integral: obtiene orden por identificador (BE-INT-031)
    @Test
    void getsOrderById() throws Exception {
        when(salesApplicationService.getOrderById(AUTHENTICATED_USER_ID, 9L))
                .thenReturn(OrderResponse.builder()
                        .id(9L)
                        .orderNumber("77-001")
                        .tableIdentifier("Mesa 4")
                        .lineItems(List.of())
                        .totalAmount(new BigDecimal("45.50"))
                        .status(Order.OrderStatus.PENDIENTE)
                        .build());

        mockMvc.perform(get("/api/orders/9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(9))
                .andExpect(jsonPath("$.data.tableIdentifier").value("Mesa 4"));

        verify(salesApplicationService).getOrderById(AUTHENTICATED_USER_ID, 9L);
    }
    // fin prueba

    // Prueba integral: avanza orden pendiente a entregada (BE-INT-032)
    @Test
    void advancesOrderStatus() throws Exception {
        when(salesApplicationService.advanceOrderStatus(AUTHENTICATED_USER_ID, 9L))
                .thenReturn(OrderResponse.builder()
                        .id(9L)
                        .orderNumber("77-001")
                        .tableIdentifier("Mesa 4")
                        .lineItems(List.of())
                        .totalAmount(new BigDecimal("45.50"))
                        .status(Order.OrderStatus.ENTREGADA)
                        .build());

        mockMvc.perform(put("/api/orders/9/advance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Order status advanced successfully"))
                .andExpect(jsonPath("$.data.status").value("ENTREGADA"));

        verify(salesApplicationService).advanceOrderStatus(AUTHENTICATED_USER_ID, 9L);
    }
    // fin prueba

    // Prueba integral: cancela orden pendiente (BE-INT-033)
    @Test
    void cancelsOrder() throws Exception {
        when(salesApplicationService.cancelOrder(AUTHENTICATED_USER_ID, 9L))
                .thenReturn(OrderResponse.builder()
                        .id(9L)
                        .orderNumber("77-001")
                        .tableIdentifier("Mesa 4")
                        .lineItems(List.of())
                        .totalAmount(new BigDecimal("45.50"))
                        .status(Order.OrderStatus.CANCELADA)
                        .build());

        mockMvc.perform(put("/api/orders/9/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Order cancelled successfully"))
                .andExpect(jsonPath("$.data.status").value("CANCELADA"));

        verify(salesApplicationService).cancelOrder(AUTHENTICATED_USER_ID, 9L);
    }
    // fin prueba

    // Prueba integral: elimina orden existente (BE-INT-034)
    @Test
    void deletesOrder() throws Exception {
        mockMvc.perform(delete("/api/orders/9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Order deleted successfully"));

        verify(salesApplicationService).deleteOrder(AUTHENTICATED_USER_ID, 9L);
    }
    // fin prueba
}
