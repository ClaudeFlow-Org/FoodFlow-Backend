package com.foodflow.finance.application;

import com.foodflow.common.domain.ValidationException;
import com.foodflow.inventory.domain.InventoryPurchase;
import com.foodflow.inventory.domain.InventoryPurchaseRepository;
import com.foodflow.sales.domain.Order;
import com.foodflow.sales.domain.OrderLineItem;
import com.foodflow.sales.domain.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinanceApplicationServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private InventoryPurchaseRepository inventoryPurchaseRepository;

    // Prueba unitaria: calcula dashboard con ingresos entregados y gastos (BE-UT-027)
    @Test
    void calculatesDashboardFromDeliveredOrdersAndPurchases() {
        FinanceApplicationService service = new FinanceApplicationService(orderRepository, inventoryPurchaseRepository);
        when(orderRepository.findByUserIdAndDateBetween(eq(77L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(
                        order(Order.OrderStatus.ENTREGADA, "120.00", lineItem(1L, "Ceviche", "40.00", 3)),
                        order(Order.OrderStatus.PENDIENTE, "90.00", lineItem(2L, "Lomo", "45.00", 2))
                ));
        when(inventoryPurchaseRepository.findByUserIdAndPurchasedAtBetween(eq(77L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(purchase("Pescados", "50.00")));

        DashboardResponse response = service.getDashboard(77L, "DAILY");

        assertThat(response.getPeriod()).isEqualTo("DAILY");
        assertThat(response.getTotalIncome()).isEqualByComparingTo("120.00");
        assertThat(response.getTotalExpenses()).isEqualByComparingTo("50.00");
        assertThat(response.getNetProfit()).isEqualByComparingTo("70.00");
        assertThat(response.getTop5Dishes()).hasSize(1);
        assertThat(response.getTop5Dishes().get(0).getDishName()).isEqualTo("Ceviche");
    }
    // fin prueba

    // Prueba unitaria: calcula desglose de gastos por categoria (BE-UT-028)
    @Test
    void calculatesExpenseBreakdownForFinancialReport() {
        FinanceApplicationService service = new FinanceApplicationService(orderRepository, inventoryPurchaseRepository);
        when(orderRepository.findByUserIdAndDateBetween(eq(77L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(order(Order.OrderStatus.ENTREGADA, "80.00", lineItem(1L, "Menu", "40.00", 2))));
        when(inventoryPurchaseRepository.findByUserIdAndPurchasedAtBetween(eq(77L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(
                        purchase("VEGETABLES", "30.00"),
                        purchase("", "10.00")
                ));

        FinancialReportResponse report = service.getFinancialReport(77L, "WEEKLY");

        assertThat(report.getPeriod()).isEqualTo("WEEKLY");
        assertThat(report.getMetrics().getTotalIncome()).isEqualByComparingTo("80.00");
        assertThat(report.getMetrics().getTotalExpenses()).isEqualByComparingTo("40.00");
        assertThat(report.getExpenseBreakdown())
                .extracting(ExpenseCategoryResponse::getName)
                .contains("Vegetales", "Sin categoria");
    }
    // fin prueba

    // Prueba unitaria: rechaza periodo financiero invalido (BE-UT-029)
    @Test
    void rejectsInvalidPeriod() {
        FinanceApplicationService service = new FinanceApplicationService(orderRepository, inventoryPurchaseRepository);

        assertThatThrownBy(() -> service.getDashboard(77L, "YEARLY"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("period: Period must be DAILY, WEEKLY, or MONTHLY");
    }
    // fin prueba

    private Order order(Order.OrderStatus status, String total, OrderLineItem item) {
        return Order.builder()
                .status(status)
                .totalAmount(new BigDecimal(total))
                .lineItems(List.of(item))
                .orderDate(LocalDateTime.now())
                .build();
    }

    private OrderLineItem lineItem(Long dishId, String dishName, String unitPrice, int quantity) {
        return OrderLineItem.builder()
                .dishId(dishId)
                .dishName(dishName)
                .unitPrice(new BigDecimal(unitPrice))
                .quantity(quantity)
                .build();
    }

    private InventoryPurchase purchase(String category, String totalCost) {
        return InventoryPurchase.builder()
                .category(category)
                .totalCost(new BigDecimal(totalCost))
                .purchasedAt(LocalDateTime.now())
                .build();
    }
}
