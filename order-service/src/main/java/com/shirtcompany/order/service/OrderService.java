package com.shirtcompany.order.service;

import com.shirtcompany.order.model.Order;
import com.shirtcompany.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public Flux<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Mono<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Mono<Order> createOrder(Order order) {
        // Lógica de validación, gestión de stock, etc.
        order.setStatus("CREATED");
        return orderRepository.save(order);
    }

    public Mono<Order> updateOrder(Long id, Order updatedOrder) {
        return orderRepository.findById(id)
                .flatMap(existingOrder -> {
                    existingOrder.setUserId(updatedOrder.getUserId());
                    existingOrder.setStatus(updatedOrder.getStatus());
                    // Aquí podrías actualizar los items si lo requieres.
                    return orderRepository.save(existingOrder);
                });
    }

    public Mono<Void> cancelOrder(Long id) {
        // En lugar de eliminar, actualizamos el estado a CANCELLED
        return orderRepository.findById(id)
                .flatMap(order -> {
                    order.setStatus("CANCELLED");
                    return orderRepository.save(order);
                })
                .then();
    }
}