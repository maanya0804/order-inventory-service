package com.example.orderservice.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PlaceOrderRequest(
        @NotBlank String customerName,
        @NotEmpty @Valid List<OrderLine> items) {

    public record OrderLine(
            @NotNull Long productId,
            @Min(1) int quantity) {
    }
}