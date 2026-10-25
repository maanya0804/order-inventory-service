package com.example.orderservice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.orderservice.error.InsufficientStockException;
import com.example.orderservice.order.OrderService;
import com.example.orderservice.order.PlaceOrderRequest;
import com.example.orderservice.product.Product;
import com.example.orderservice.product.ProductRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OrderConcurrencyTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderService orderService;

    @Test
    void neverOversellsUnderConcurrentOrders() throws Exception {
        Product saved = productRepository.save(new Product("Concurrency Test Item", new BigDecimal("50.00"), 10));
        Long productId = saved.getId();

        ExecutorService pool = Executors.newFixedThreadPool(20);
        List<Future<Boolean>> results = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            results.add(pool.submit(() -> {
                try {
                    orderService.placeOrder(new PlaceOrderRequest("Buyer",
                            List.of(new PlaceOrderRequest.OrderLine(productId, 1))));
                    return true;
                } catch (InsufficientStockException e) {
                    return false;
                }
            }));
        }

        int successes = 0;
        for (Future<Boolean> f : results) {
            if (f.get()) {
                successes++;
            }
        }
        pool.shutdown();

        int stockLeft = productRepository.findById(productId).orElseThrow().getStock();
        assertEquals(10, successes);
        assertEquals(0, stockLeft);
    }
}