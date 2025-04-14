package com.shirtcompany.order.repository;

import com.shirtcompany.order.model.Order;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {
}