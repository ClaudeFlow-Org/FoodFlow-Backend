package com.foodflow.billing.presentation;

import com.foodflow.billing.application.BillingApplicationService;
import com.foodflow.billing.application.SubscriptionPlanResponse;
import com.foodflow.billing.application.SubscriptionResponse;
import com.foodflow.common.presentation.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static com.foodflow.ControllerTestSupport.AUTHENTICATED_USER_ID;
import static com.foodflow.ControllerTestSupport.authenticatedUserArgumentResolver;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BillingApplicationService billingApplicationService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new SubscriptionController(billingApplicationService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(authenticatedUserArgumentResolver())
                .build();
    }

    // Prueba integral: lista planes de suscripcion disponibles (BE-INT-013)
    @Test
    void listsSubscriptionPlans() throws Exception {
        when(billingApplicationService.getAvailablePlans()).thenReturn(List.of(
                SubscriptionPlanResponse.builder()
                        .name("Free")
                        .monthlyPrice(0.0)
                        .benefits(List.of("Basic dashboard access"))
                        .build(),
                SubscriptionPlanResponse.builder()
                        .name("Premium")
                        .monthlyPrice(79.99)
                        .benefits(List.of("Advanced analytics"))
                        .build()
        ));

        mockMvc.perform(get("/api/subscriptions/plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Free"))
                .andExpect(jsonPath("$.data[1].monthlyPrice").value(79.99));
    }
    // fin prueba

    // Prueba integral: suscribe usuario autenticado a plan valido (BE-INT-014)
    @Test
    void subscribesAuthenticatedUser() throws Exception {
        SubscriptionResponse response = SubscriptionResponse.builder()
                .id("sub-1")
                .plan("Premium")
                .status("ACTIVE")
                .startDate(LocalDateTime.of(2026, 5, 9, 10, 0))
                .build();
        when(billingApplicationService.subscribe(eq(AUTHENTICATED_USER_ID), any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/subscriptions/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "plan": "PREMIUM"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Subscription created successfully"))
                .andExpect(jsonPath("$.data.plan").value("Premium"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));

        verify(billingApplicationService).subscribe(eq(AUTHENTICATED_USER_ID), any());
    }
    // fin prueba

    // Prueba integral: rechaza plan de suscripcion invalido (BE-INT-015)
    @Test
    void rejectsInvalidSubscriptionPlan() throws Exception {
        mockMvc.perform(post("/api/subscriptions/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "plan": "ENTERPRISE"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isArray());
    }
    // fin prueba

    // Prueba integral: consulta suscripcion actual del usuario (BE-INT-035)
    @Test
    void getsCurrentSubscription() throws Exception {
        SubscriptionResponse response = SubscriptionResponse.builder()
                .id("sub-current")
                .plan("Standard")
                .status("ACTIVE")
                .startDate(LocalDateTime.of(2026, 5, 9, 10, 0))
                .endDate(LocalDateTime.of(2026, 6, 9, 10, 0))
                .build();
        when(billingApplicationService.getCurrentSubscription(AUTHENTICATED_USER_ID))
                .thenReturn(response);

        mockMvc.perform(get("/api/subscriptions/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("sub-current"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));

        verify(billingApplicationService).getCurrentSubscription(AUTHENTICATED_USER_ID);
    }
    // fin prueba

    // Prueba integral: cancela suscripcion actual del usuario (BE-INT-036)
    @Test
    void cancelsCurrentSubscription() throws Exception {
        SubscriptionResponse response = SubscriptionResponse.builder()
                .id("sub-current")
                .plan("Standard")
                .status("CANCELLED")
                .startDate(LocalDateTime.of(2026, 5, 9, 10, 0))
                .endDate(LocalDateTime.of(2026, 6, 9, 10, 0))
                .cancellationDate(LocalDateTime.of(2026, 5, 10, 12, 0))
                .build();
        when(billingApplicationService.cancelSubscription(AUTHENTICATED_USER_ID))
                .thenReturn(response);

        mockMvc.perform(post("/api/subscriptions/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Subscription cancelled successfully"))
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));

        verify(billingApplicationService).cancelSubscription(AUTHENTICATED_USER_ID);
    }
    // fin prueba
}
