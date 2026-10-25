package com.example.orderservice.order;

import com.example.orderservice.error.InsufficientStockException;
import com.example.orderservice.error.NotFoundException;
import com.example.orderservice.product.Product;
import com.example.orderservice.product.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public OrderService(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderResponse placeOrder(PlaceOrderRequest request) {
        Order order = new Order(request.customerName());
        BigDecimal total = BigDecimal.ZERO;

        for (PlaceOrderRequest.OrderLine line : request.items()) {
            Product product = productRepository.findById(line.productId())
                    .orElseThrow(() -> new NotFoundException("Product " + line.productId() + " not found"));

            int updated = productRepository.reduceStock(product.getId(), line.quantity());
            if (updated == 0) {
                throw new InsufficientStockException(
                        "Not enough stock for product " + product.getId() + " (" + product.getName() + ")");
            }

            order.addItem(new OrderItem(product.getId(), line.quantity(), product.getPrice()));
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(line.quantity())));
        }

        order.setTotalAmount(total);
        return OrderResponse.from(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long id) {
        return orderRepository.findById(id)
                .map(OrderResponse::from)
                .orElseThrow(() -> new NotFoundException("Order " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> listOrders() {
        return orderRepository.findAll().stream().map(OrderResponse::from).toList();
    }
}