package com.foodflow.sales.application;

import com.foodflow.catalog.domain.Dish;
import com.foodflow.catalog.domain.DishRepository;
import com.foodflow.common.domain.NotFoundException;
import com.foodflow.common.domain.ValidationException;
import com.foodflow.sales.domain.Order;
import com.foodflow.sales.domain.OrderLineItem;
import com.foodflow.sales.domain.OrderRepository;
import com.foodflow.sales.domain.OrderSequence;
import com.foodflow.sales.domain.OrderSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Transactional
public class SalesApplicationService {

    private final OrderRepository orderRepository;
    private final DishRepository dishRepository;
    private final OrderSequenceRepository orderSequenceRepository;

    public OrderResponse createOrder(Long userId, OrderRequest request) {
        if (request.getLineItems() == null || request.getLineItems().isEmpty()) {
            throw new ValidationException("lineItems", "At least one line item is required");
        }

        // Get or create order sequence for this user
        OrderSequence sequence = orderSequenceRepository.findByUserId(userId)
                .orElseGet(() -> OrderSequence.builder()
                        .userId(userId)
                        .nextValue(1L)
                        .build());

        // Get next sequence number and update
        Long sequenceNumber = sequence.getNextAndIncrement();
        orderSequenceRepository.save(sequence);

        // Generate unique order number: {userId}-{sequenceNumber}
        String orderNumber = Order.generateOrderNumber(userId, sequenceNumber);

        List<OrderLineItem> lineItems = request.getLineItems().stream()
                .map(itemRequest -> {
                    Dish dish = dishRepository.findById(com.foodflow.catalog.domain.Dish.DishId.of(itemRequest.getDishId()))
                            .orElseThrow(() -> new NotFoundException("Dish", "id " + itemRequest.getDishId()));

                    if (!dish.getUserId().equals(userId)) {
                        throw new ValidationException("You do not have access to dish with id " + itemRequest.getDishId());
                    }

                    return OrderLineItem.builder()
                            .dishId(dish.getId().value())
                            .dishName(dish.getName())
                            .unitPrice(dish.getPrice())
                            .quantity(itemRequest.getQuantity())
                            .build();
                })
                .collect(Collectors.toList());

        Order order = Order.builder()
                .id(Order.OrderId.empty())
                .userId(userId)
                .tableIdentifier(request.getTableIdentifier())
                .orderDate(LocalDateTime.now())
                .lineItems(lineItems)
                .totalAmount(BigDecimal.ZERO)
                .status(Order.OrderStatus.PENDIENTE)
                .orderNumber(orderNumber)
                .build();

        order.calculateTotal();

        Order savedOrder = orderRepository.save(order);

        return toResponse(savedOrder);
    }

    public List<OrderResponse> getAllOrders(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findById(Order.OrderId.of(orderId))
                .orElseThrow(() -> new NotFoundException("Order", "id " + orderId));

        if (!order.getUserId().equals(userId)) {
            throw new ValidationException("You do not have access to this order");
        }

        return toResponse(order);
    }

    public void deleteOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(Order.OrderId.of(orderId))
                .orElseThrow(() -> new NotFoundException("Order", "id " + orderId));

        if (!order.getUserId().equals(userId)) {
            throw new ValidationException("You do not have access to this order");
        }

        orderRepository.delete(Order.OrderId.of(orderId));
    }

    private OrderResponse toResponse(Order order) {
        List<OrderLineItemResponse> lineItemResponses = order.getLineItems().stream()
                .map(item -> OrderLineItemResponse.builder()
                        .dishId(item.getDishId())
                        .dishName(item.getDishName())
                        .unitPrice(item.getUnitPrice())
                        .quantity(item.getQuantity())
                        .lineTotal(item.getLineTotal())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId().value())
                .orderNumber(order.getOrderNumber())
                .tableIdentifier(order.getTableIdentifier())
                .orderDate(order.getOrderDate())
                .lineItems(lineItemResponses)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus() != null ? order.getStatus() : Order.OrderStatus.PENDIENTE)
                .build();
    }

    public OrderResponse updateOrderStatus(Long userId, Long orderId, Order.OrderStatus newStatus) {
        Order order = orderRepository.findById(Order.OrderId.of(orderId))
                .orElseThrow(() -> new NotFoundException("Order", "id " + orderId));

        if (!order.getUserId().equals(userId)) {
            throw new ValidationException("You do not have access to this order");
        }

        order.setStatus(newStatus);
        Order savedOrder = orderRepository.save(order);

        return toResponse(savedOrder);
    }

    public OrderResponse advanceOrderStatus(Long userId, Long orderId) {
        Order order = orderRepository.findById(Order.OrderId.of(orderId))
                .orElseThrow(() -> new NotFoundException("Order", "id " + orderId));

        if (!order.getUserId().equals(userId)) {
            throw new ValidationException("You do not have access to this order");
        }

        order.advanceStatus();
        Order savedOrder = orderRepository.save(order);

        return toResponse(savedOrder);
    }

    public OrderResponse cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(Order.OrderId.of(orderId))
                .orElseThrow(() -> new NotFoundException("Order", "id " + orderId));

        if (!order.getUserId().equals(userId)) {
            throw new ValidationException("You do not have access to this order");
        }

        order.cancel();
        Order savedOrder = orderRepository.save(order);

        return toResponse(savedOrder);
    }
}
