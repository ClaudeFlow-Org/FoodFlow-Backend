package com.foodflow.finance.presentation;

import com.foodflow.common.domain.ValidationException;
import com.foodflow.common.presentation.GlobalExceptionHandler;
import com.foodflow.finance.application.DashboardResponse;
import com.foodflow.finance.application.FinanceApplicationService;
import com.foodflow.finance.application.FinancialMetricsResponse;
import com.foodflow.finance.application.FinancialReportResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.foodflow.ControllerTestSupport.AUTHENTICATED_USER_ID;
import static com.foodflow.ControllerTestSupport.authenticatedUserArgumentResolver;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FinanceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FinanceApplicationService financeApplicationService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new FinanceController(financeApplicationService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(authenticatedUserArgumentResolver())
                .build();
    }

    // Prueba integral: consulta dashboard financiero por periodo (BE-INT-016)
    @Test
    void getsDashboardForRequestedPeriod() throws Exception {
        when(financeApplicationService.getDashboard(AUTHENTICATED_USER_ID, "MONTHLY"))
                .thenReturn(DashboardResponse.builder()
                        .period("MONTHLY")
                        .startDate(LocalDateTime.of(2026, 5, 1, 0, 0))
                        .endDate(LocalDateTime.of(2026, 6, 1, 0, 0))
                        .totalIncome(new BigDecimal("900.00"))
                        .totalExpenses(new BigDecimal("350.00"))
                        .netProfit(new BigDecimal("550.00"))
                        .orderCount(20L)
                        .top5Dishes(List.of())
                        .build());

        mockMvc.perform(get("/api/finance/dashboard").param("period", "MONTHLY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.period").value("MONTHLY"))
                .andExpect(jsonPath("$.data.totalIncome").value(900.00))
                .andExpect(jsonPath("$.data.netProfit").value(550.00));

        verify(financeApplicationService).getDashboard(AUTHENTICATED_USER_ID, "MONTHLY");
    }
    // fin prueba

    // Prueba integral: consulta reporte financiero detallado (BE-INT-017)
    @Test
    void getsFinancialReport() throws Exception {
        when(financeApplicationService.getFinancialReport(AUTHENTICATED_USER_ID, "WEEKLY"))
                .thenReturn(FinancialReportResponse.builder()
                        .period("WEEKLY")
                        .startDate(LocalDateTime.of(2026, 5, 4, 0, 0))
                        .endDate(LocalDateTime.of(2026, 5, 11, 0, 0))
                        .metrics(FinancialMetricsResponse.builder()
                                .totalIncome(new BigDecimal("480.00"))
                                .totalExpenses(new BigDecimal("155.00"))
                                .netProfit(new BigDecimal("325.00"))
                                .build())
                        .topDishes(List.of())
                        .expenseBreakdown(List.of())
                        .orderCount(9L)
                        .build());

        mockMvc.perform(get("/api/finance/reports").param("period", "WEEKLY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.period").value("WEEKLY"))
                .andExpect(jsonPath("$.data.metrics.netProfit").value(325.00))
                .andExpect(jsonPath("$.data.orderCount").value(9));
    }
    // fin prueba

    // Prueba integral: propaga validacion de periodo financiero invalido (BE-INT-018)
    @Test
    void returnsBadRequestWhenPeriodIsInvalid() throws Exception {
        when(financeApplicationService.getDashboard(AUTHENTICATED_USER_ID, "YEARLY"))
                .thenThrow(new ValidationException("period", "Period must be DAILY, WEEKLY, or MONTHLY"));

        mockMvc.perform(get("/api/finance/dashboard").param("period", "YEARLY"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("period: Period must be DAILY, WEEKLY, or MONTHLY"));
    }
    // fin prueba
}
