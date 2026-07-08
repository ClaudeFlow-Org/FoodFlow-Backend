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

    private static final int MAX_TABLE_IDENTIFIER_LENGTH = 50;
    private static final int MAX_ORDER_LINE_ITEMS = 30;
    private static final int MAX_ORDER_ITEM_QUANTITY = 100;
    private static final BigDecimal MAX_ORDER_TOTAL = new BigDecimal("9999999999.99");

    private final OrderRepository orderRepository;
    private final DishRepository dishRepository;
    private final DishRecipeItemRepository dishRecipeItemRepository;
    private final ProductRepository productRepository;
    private final OrderSequenceRepository orderSequenceRepository;
    private final com.foodflow.billing.application.PlanLimitService planLimitService;

    public OrderResponse createOrder(Long userId, OrderRequest request) {
        validateOrderRequest(request);

        planLimitService.assertCanCreateOrder(userId, countOrdersThisMonth(userId));

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
                    if (itemRequest.getQuantity() > MAX_ORDER_ITEM_QUANTITY) {
                        throw new ValidationException("lineItems.quantity", "Quantity must not exceed " + MAX_ORDER_ITEM_QUANTITY);
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

        // Get or create order sequence for this user after stock validation succeeds
        OrderSequence sequence = orderSequenceRepository.findByUserId(userId)
                .orElseGet(() -> OrderSequence.builder()
                        .userId(userId)
                        .nextValue(1L)
                        .build());

        Long sequenceNumber = sequence.getNextAndIncrement();
        orderSequenceRepository.save(sequence);

        // Generate unique order number: {userId}-{sequenceNumber}
        String orderNumber = Order.generateOrderNumber(userId, sequenceNumber);

        Order order = Order.builder()
                .id(Order.OrderId.empty())
                .userId(userId)
                .tableIdentifier(request.getTableIdentifier().trim())
                .orderDate(LocalDateTime.now())
                .lineItems(lineItems)
                .totalAmount(BigDecimal.ZERO)
                .status(Order.OrderStatus.PENDIENTE)
                .orderNumber(orderNumber)
                .build();

        order.calculateTotal();
        validateOrderTotal(order.getTotalAmount());

        Order savedOrder = orderRepository.save(order);

        return toResponse(savedOrder);
    }

    public List<OrderResponse> getAllOrders(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private long countOrdersThisMonth(Long userId) {
        LocalDateTime startOfMonth = LocalDateTime.now()
                .withDayOfMonth(1)
                .toLocalDate()
                .atStartOfDay();
        return orderRepository.findByUserId(userId).stream()
                .filter(order -> order.getOrderDate() != null && !order.getOrderDate().isBefore(startOfMonth))
                .count();
    }

    private void validateOrderRequest(OrderRequest request) {
        if (request == null) {
            throw new ValidationException("order", "Order information is required");
        }
        if (request.getTableIdentifier() == null || request.getTableIdentifier().isBlank()) {
            throw new ValidationException("tableIdentifier", "Table identifier is required");
        }
        if (request.getTableIdentifier().trim().length() > MAX_TABLE_IDENTIFIER_LENGTH) {
            throw new ValidationException("tableIdentifier", "Table identifier must not exceed " + MAX_TABLE_IDENTIFIER_LENGTH + " characters");
        }
        if (request.getLineItems() == null || request.getLineItems().isEmpty()) {
            throw new ValidationException("lineItems", "At least one line item is required");
        }
        if (request.getLineItems().size() > MAX_ORDER_LINE_ITEMS) {
            throw new ValidationException("lineItems", "Order must not exceed " + MAX_ORDER_LINE_ITEMS + " line items");
        }
    }

    private void validateOrderTotal(BigDecimal totalAmount) {
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("totalAmount", "Order total must be greater than 0");
        }
        if (totalAmount.compareTo(MAX_ORDER_TOTAL) > 0) {
            throw new ValidationException("totalAmount", "Order total must not exceed " + MAX_ORDER_TOTAL.stripTrailingZeros().toPlainString());
        }
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
        Map<Long, BigDecimal> reservedByProduct = calculateReservedStockByProduct(userId, productsById);
        validateRequiredAvailableStock(requiredByProduct, productsById, reservedByProduct);
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

    private void validateRequiredAvailableStock(Map<Long, BigDecimal> requiredByProduct,
                                                Map<Long, Product> productsById,
                                                Map<Long, BigDecimal> reservedByProduct) {
        for (Map.Entry<Long, BigDecimal> entry : requiredByProduct.entrySet()) {
            Product product = productsById.get(entry.getKey());
            if (product == null) {
                throw new ValidationException("stockLevel", "A recipe product no longer exists in inventory");
            }

            BigDecimal stockLevel = product.getStockLevel() != null ? product.getStockLevel() : BigDecimal.ZERO;
            BigDecimal reservedStock = reservedByProduct.getOrDefault(entry.getKey(), BigDecimal.ZERO);
            BigDecimal availableStock = stockLevel.subtract(reservedStock).max(BigDecimal.ZERO);
            BigDecimal requiredStock = entry.getValue();
            if (availableStock.compareTo(requiredStock) < 0) {
                throw new ValidationException(
                        "stockLevel",
                        "Insufficient available stock for " + product.getName() + ". Required " +
                                requiredStock.stripTrailingZeros().toPlainString() + " " + product.getUnitOfMeasure() +
                        ", available " + availableStock.stripTrailingZeros().toPlainString() + " " + product.getUnitOfMeasure() +
                        " after pending orders"
                );
            }
        }
    }

    private Map<Long, BigDecimal> calculateReservedStockByProduct(Long userId, Map<Long, Product> productsById) {
        List<OrderLineItem> pendingLineItems = orderRepository.findByUserId(userId).stream()
                .filter(this::isPendingOrder)
                .flatMap(order -> order.getLineItems().stream())
                .toList();
        if (pendingLineItems.isEmpty()) {
            return Map.of();
        }

        return calculateRequiredStockByProduct(userId, pendingLineItems, productsById);
    }

    private boolean isPendingOrder(Order order) {
        return order.getStatus() == null || order.getStatus() == Order.OrderStatus.PENDIENTE;
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
