package com.example.orderservice.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        String customerName,
        OrderStatus status,
        BigDecimal totalAmount,
        Instant createdAt,
        List<ItemResponse> items) {

    public record ItemResponse(Long productId, int quantity, BigDecimal unitPrice) {
    }

    public static OrderResponse from(Order o) {
        List<ItemResponse> items = o.getItems().stream()
                .map(i -> new ItemResponse(i.getProductId(), i.getQuantity(), i.getUnitPrice()))
                .toList();
        return new OrderResponse(o.getId(), o.getCustomerName(), o.getStatus(),
                o.getTotalAmount(), o.getCreatedAt(), items);
    }
}