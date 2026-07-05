package com.foodflow.sales.application;

import com.foodflow.catalog.domain.DishRecipeItem;
import com.foodflow.catalog.domain.DishRecipeItemRepository;
import com.foodflow.catalog.domain.Dish;
import com.foodflow.catalog.domain.DishRepository;
import com.foodflow.common.domain.MeasurementUnitConverter;
import com.foodflow.common.domain.NotFoundException;
import com.foodflow.common.domain.ValidationException;
import com.foodflow.inventory.domain.Product;
import com.foodflow.inventory.domain.ProductRepository;
import com.foodflow.sales.domain.Order;
import com.foodflow.sales.domain.OrderLineItem;
import com.foodflow.sales.domain.OrderRepository;
import com.foodflow.sales.domain.OrderSequence;
import com.foodflow.sales.domain.OrderSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Transactional
public class SalesApplicationService {

    private final OrderRepository orderRepository;
    private final DishRepository dishRepository;
    private final DishRecipeItemRepository dishRecipeItemRepository;
    private final ProductRepository productRepository;
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
                    if (itemRequest.getQuantity() == null || itemRequest.getQuantity() <= 0) {
                        throw new ValidationException("lineItems.quantity", "Quantity must be at least 1");
                    }

                    return OrderLineItem.builder()
                            .dishId(dish.getId().value())
                            .dishName(dish.getName())
                            .unitPrice(dish.getPrice())
                            .quantity(itemRequest.getQuantity())
                            .build();
                })
                .collect(Collectors.toList());

        validateInventoryAvailabilityForOrder(userId, lineItems);

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
        if (order.getStatus() == Order.OrderStatus.ENTREGADA) {
            throw new ValidationException("Delivered orders cannot be deleted because inventory was already discounted");
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

        Order.OrderStatus previousStatus = order.getStatus() != null ? order.getStatus() : Order.OrderStatus.PENDIENTE;
        if (previousStatus == Order.OrderStatus.ENTREGADA && newStatus != Order.OrderStatus.ENTREGADA) {
            throw new ValidationException("Delivered orders cannot change status because inventory was already discounted");
        }
        if (previousStatus == Order.OrderStatus.CANCELADA && newStatus != Order.OrderStatus.CANCELADA) {
            throw new ValidationException("Cancelled orders cannot change status");
        }
        if (previousStatus != Order.OrderStatus.ENTREGADA && newStatus == Order.OrderStatus.ENTREGADA) {
            discountInventoryForDeliveredOrder(userId, order);
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

        Order.OrderStatus previousStatus = order.getStatus() != null ? order.getStatus() : Order.OrderStatus.PENDIENTE;
        order.advanceStatus();
        if (previousStatus != Order.OrderStatus.ENTREGADA && order.getStatus() == Order.OrderStatus.ENTREGADA) {
            discountInventoryForDeliveredOrder(userId, order);
        }
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

    private void discountInventoryForDeliveredOrder(Long userId, Order order) {
        Map<Long, Product> productsById = productRepository.findByUserId(userId).stream()
                .collect(Collectors.toMap(product -> product.getId().value(), product -> product));
        Map<Long, BigDecimal> requiredByProduct = calculateRequiredStockByProduct(userId, order.getLineItems(), productsById);

        if (requiredByProduct.isEmpty()) {
            return;
        }

        validateRequiredStock(requiredByProduct, productsById);

        for (Map.Entry<Long, BigDecimal> entry : requiredByProduct.entrySet()) {
            Product product = productsById.get(entry.getKey());
            product.setStockLevel(product.getStockLevel().subtract(entry.getValue()));
            product.setUpdatedAt(LocalDateTime.now());
            productRepository.save(product);
        }
    }

    private void validateInventoryAvailabilityForOrder(Long userId, List<OrderLineItem> lineItems) {
        Map<Long, Product> productsById = productRepository.findByUserId(userId).stream()
                .collect(Collectors.toMap(product -> product.getId().value(), product -> product));
        Map<Long, BigDecimal> requiredByProduct = calculateRequiredStockByProduct(userId, lineItems, productsById);
        validateRequiredStock(requiredByProduct, productsById);
    }

    private Map<Long, BigDecimal> calculateRequiredStockByProduct(Long userId, List<OrderLineItem> lineItems,
                                                                  Map<Long, Product> productsById) {
        if (lineItems == null || lineItems.isEmpty()) {
            return Map.of();
        }

        Map<Long, Integer> orderedQuantitiesByDish = lineItems.stream()
                .collect(Collectors.toMap(
                        OrderLineItem::getDishId,
                        OrderLineItem::getQuantity,
                        Integer::sum
                ));

        List<DishRecipeItem> recipeItems = dishRecipeItemRepository.findByUserIdAndDishIdIn(
                userId,
                orderedQuantitiesByDish.keySet().stream().toList()
        );
        if (recipeItems.isEmpty()) {
            return Map.of();
        }

        Map<Long, BigDecimal> requiredByProduct = new HashMap<>();
        for (DishRecipeItem recipeItem : recipeItems) {
            Integer orderedQuantity = orderedQuantitiesByDish.get(recipeItem.getDishId());
            if (orderedQuantity == null || orderedQuantity <= 0) {
                continue;
            }

            Product product = productsById.get(recipeItem.getProductId());
            if (product == null) {
                throw new ValidationException("stockLevel", "A recipe product no longer exists in inventory");
            }

            BigDecimal requiredQuantity = convertRecipeQuantityToStockUnit(recipeItem, product)
                    .multiply(BigDecimal.valueOf(orderedQuantity));
            requiredByProduct.merge(recipeItem.getProductId(), requiredQuantity, BigDecimal::add);
        }

        return requiredByProduct;
    }

    private void validateRequiredStock(Map<Long, BigDecimal> requiredByProduct, Map<Long, Product> productsById) {
        for (Map.Entry<Long, BigDecimal> entry : requiredByProduct.entrySet()) {
            Product product = productsById.get(entry.getKey());
            if (product == null) {
                throw new ValidationException("stockLevel", "A recipe product no longer exists in inventory");
            }

            BigDecimal availableStock = product.getStockLevel() != null ? product.getStockLevel() : BigDecimal.ZERO;
            BigDecimal requiredStock = entry.getValue();
            if (availableStock.compareTo(requiredStock) < 0) {
                throw new ValidationException(
                        "stockLevel",
                        "Insufficient stock for " + product.getName() + ". Required " +
                                requiredStock.stripTrailingZeros().toPlainString() + " " + product.getUnitOfMeasure() +
                        ", available " + availableStock.stripTrailingZeros().toPlainString() + " " + product.getUnitOfMeasure()
                );
            }
        }
    }

    private BigDecimal convertRecipeQuantityToStockUnit(DishRecipeItem recipeItem, Product product) {
        String requiredUnit = recipeItem.getRequiredUnitOfMeasure();
        if (requiredUnit == null || requiredUnit.isBlank()) {
            requiredUnit = resolveLegacyRecipeUnit(recipeItem, product);
        }

        try {
            return MeasurementUnitConverter.convert(
                    recipeItem.getRequiredQuantity(),
                    requiredUnit,
                    product.getUnitOfMeasure()
            );
        } catch (IllegalArgumentException exception) {
            throw new ValidationException(
                    "recipeItems.requiredUnitOfMeasure",
                    "Recipe unit " + requiredUnit + " is not compatible with inventory unit " + product.getUnitOfMeasure()
            );
        }
    }

    private String resolveLegacyRecipeUnit(DishRecipeItem recipeItem, Product product) {
        String stockUnit = MeasurementUnitConverter.normalizedLabel(product.getUnitOfMeasure());
        BigDecimal stockLevel = product.getStockLevel() != null ? product.getStockLevel() : BigDecimal.ZERO;
        BigDecimal requiredQuantity = recipeItem.getRequiredQuantity();
        if (requiredQuantity != null && stockLevel.compareTo(BigDecimal.ZERO) > 0
                && requiredQuantity.compareTo(stockLevel) > 0) {
            if ("kg".equals(stockUnit)) {
                return "g";
            }
            if ("l".equals(stockUnit)) {
                return "ml";
            }
        }
        return stockUnit;
    }
}
