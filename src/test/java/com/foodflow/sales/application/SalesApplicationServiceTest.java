package com.foodflow.sales.application;

import com.foodflow.catalog.domain.Dish;
import com.foodflow.catalog.domain.DishRepository;
import com.foodflow.catalog.domain.DishRecipeItem;
import com.foodflow.catalog.domain.DishRecipeItemRepository;
import com.foodflow.billing.application.PlanLimitService;
import com.foodflow.common.domain.ValidationException;
import com.foodflow.inventory.domain.Product;
import com.foodflow.inventory.domain.ProductRepository;
import com.foodflow.sales.domain.Order;
import com.foodflow.sales.domain.OrderLineItem;
import com.foodflow.sales.domain.OrderRepository;
import com.foodflow.sales.domain.OrderSequence;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Mock
    private PlanLimitService planLimitService;

    @Test
    void discountsRecipeInventoryWhenOrderIsDelivered() {
        SalesApplicationService service = new SalesApplicationService(
                orderRepository,
                dishRepository,
                dishRecipeItemRepository,
                productRepository,
                orderSequenceRepository,
                planLimitService
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

    @Test
    void reservesPendingOrderInventoryWhenCreatingNewOrder() {
        SalesApplicationService service = new SalesApplicationService(
                orderRepository,
                dishRepository,
                dishRecipeItemRepository,
                productRepository,
                orderSequenceRepository,
                planLimitService
        );
        Dish dish = Dish.builder()
                .id(Dish.DishId.of(10L))
                .name("Taco pastor")
                .price(new BigDecimal("12.00"))
                .userId(77L)
                .build();
        Product product = Product.builder()
                .id(Product.ProductId.of(44L))
                .name("Carne pastor")
                .stockLevel(new BigDecimal("1.500"))
                .unitCost(new BigDecimal("40.00"))
                .unitOfMeasure("kg")
                .userId(77L)
                .build();
        DishRecipeItem recipeItem = DishRecipeItem.builder()
                .id(DishRecipeItem.DishRecipeItemId.of(1L))
                .userId(77L)
                .dishId(10L)
                .productId(44L)
                .requiredQuantity(new BigDecimal("1000"))
                .requiredUnitOfMeasure("g")
                .build();
        Order pendingOrder = Order.builder()
                .id(Order.OrderId.of(7L))
                .userId(77L)
                .status(Order.OrderStatus.PENDIENTE)
                .lineItems(List.of(OrderLineItem.builder()
                        .dishId(10L)
                        .dishName("Taco pastor")
                        .unitPrice(new BigDecimal("12.00"))
                        .quantity(1)
                        .build()))
                .build();

        when(dishRepository.findById(Dish.DishId.of(10L))).thenReturn(Optional.of(dish));
        when(productRepository.findByUserId(77L)).thenReturn(List.of(product));
        when(dishRecipeItemRepository.findByUserIdAndDishIdIn(77L, List.of(10L))).thenReturn(List.of(recipeItem));
        when(orderRepository.findByUserId(77L)).thenReturn(List.of(pendingOrder));

        assertThatThrownBy(() -> service.createOrder(77L, OrderRequest.builder()
                .tableIdentifier("Mesa 2")
                .lineItems(List.of(OrderLineItemRequest.builder()
                        .dishId(10L)
                        .quantity(1)
                        .build()))
                .build()))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Insufficient available stock for Carne pastor");
    }

    @Test
    void createsOrderWhenReservedInventoryStillLeavesEnoughAvailableStock() {
        SalesApplicationService service = new SalesApplicationService(
                orderRepository,
                dishRepository,
                dishRecipeItemRepository,
                productRepository,
                orderSequenceRepository,
                planLimitService
        );
        Dish dish = Dish.builder()
                .id(Dish.DishId.of(10L))
                .name("Taco pastor")
                .price(new BigDecimal("12.00"))
                .userId(77L)
                .build();
        Product product = Product.builder()
                .id(Product.ProductId.of(44L))
                .name("Carne pastor")
                .stockLevel(new BigDecimal("2.000"))
                .unitCost(new BigDecimal("40.00"))
                .unitOfMeasure("kg")
                .userId(77L)
                .build();
        DishRecipeItem recipeItem = DishRecipeItem.builder()
                .id(DishRecipeItem.DishRecipeItemId.of(1L))
                .userId(77L)
                .dishId(10L)
                .productId(44L)
                .requiredQuantity(new BigDecimal("500"))
                .requiredUnitOfMeasure("g")
                .build();
        Order pendingOrder = Order.builder()
                .id(Order.OrderId.of(7L))
                .userId(77L)
                .status(Order.OrderStatus.PENDIENTE)
                .lineItems(List.of(OrderLineItem.builder()
                        .dishId(10L)
                        .dishName("Taco pastor")
                        .unitPrice(new BigDecimal("12.00"))
                        .quantity(2)
                        .build()))
                .build();

        when(dishRepository.findById(Dish.DishId.of(10L))).thenReturn(Optional.of(dish));
        when(productRepository.findByUserId(77L)).thenReturn(List.of(product));
        when(dishRecipeItemRepository.findByUserIdAndDishIdIn(77L, List.of(10L))).thenReturn(List.of(recipeItem));
        when(orderRepository.findByUserId(77L)).thenReturn(List.of(pendingOrder));
        when(orderSequenceRepository.findByUserId(77L)).thenReturn(Optional.of(OrderSequence.builder()
                .userId(77L)
                .nextValue(1L)
                .build()));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(Order.OrderId.of(8L));
            return order;
        });

        OrderResponse response = service.createOrder(77L, OrderRequest.builder()
                .tableIdentifier("Mesa 2")
                .lineItems(List.of(OrderLineItemRequest.builder()
                        .dishId(10L)
                        .quantity(1)
                        .build()))
                .build());

        assertThat(response.getId()).isEqualTo(8L);
        assertThat(response.getStatus()).isEqualTo(Order.OrderStatus.PENDIENTE);
    }
}
