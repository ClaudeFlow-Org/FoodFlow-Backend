package com.foodflow.sales.application;

import com.foodflow.catalog.domain.DishRepository;
import com.foodflow.catalog.domain.DishRecipeItem;
import com.foodflow.catalog.domain.DishRecipeItemRepository;
import com.foodflow.inventory.domain.Product;
import com.foodflow.inventory.domain.ProductRepository;
import com.foodflow.sales.domain.Order;
import com.foodflow.sales.domain.OrderLineItem;
import com.foodflow.sales.domain.OrderRepository;
import com.foodflow.sales.domain.OrderSequenceRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SalesApplicationServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private DishRepository dishRepository;

    @Mock
    private DishRecipeItemRepository dishRecipeItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderSequenceRepository orderSequenceRepository;

    @Test
    void discountsRecipeInventoryWhenOrderIsDelivered() {
        SalesApplicationService service = new SalesApplicationService(
                orderRepository,
                dishRepository,
                dishRecipeItemRepository,
                productRepository,
                orderSequenceRepository
        );
        Order order = Order.builder()
                .id(Order.OrderId.of(7L))
                .userId(77L)
                .orderNumber("77-001")
                .tableIdentifier("Mesa 1")
                .orderDate(LocalDateTime.of(2026, 6, 1, 12, 0))
                .status(Order.OrderStatus.PENDIENTE)
                .lineItems(List.of(OrderLineItem.builder()
                        .dishId(10L)
                        .dishName("Taco pastor")
                        .unitPrice(new BigDecimal("12.00"))
                        .quantity(3)
                        .build()))
                .totalAmount(new BigDecimal("36.00"))
                .build();
        Product product = Product.builder()
                .id(Product.ProductId.of(44L))
                .name("Carne pastor")
                .stockLevel(new BigDecimal("5.000"))
                .unitCost(new BigDecimal("40.00"))
                .unitOfMeasure("kg")
                .userId(77L)
                .build();

        when(orderRepository.findById(Order.OrderId.of(7L))).thenReturn(Optional.of(order));
        when(dishRecipeItemRepository.findByUserIdAndDishIdIn(77L, List.of(10L))).thenReturn(List.of(
                DishRecipeItem.builder()
                        .id(DishRecipeItem.DishRecipeItemId.of(1L))
                        .userId(77L)
                        .dishId(10L)
                        .productId(44L)
                        .requiredQuantity(new BigDecimal("500"))
                        .build()
        ));
        when(productRepository.findByUserId(77L)).thenReturn(List.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = service.advanceOrderStatus(77L, 7L);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        assertThat(productCaptor.getValue().getStockLevel()).isEqualByComparingTo("3.500");
        assertThat(response.getStatus()).isEqualTo(Order.OrderStatus.ENTREGADA);
    }
}
