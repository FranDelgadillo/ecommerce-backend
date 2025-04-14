package com.shirtcompany.payment.repository;

import com.shirtcompany.payment.model.Payment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PaymentRepository extends ReactiveCrudRepository<Payment, Long> {
}