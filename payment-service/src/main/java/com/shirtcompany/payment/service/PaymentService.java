package com.shirtcompany.payment.service;

import com.shirtcompany.payment.model.Payment;
import com.shirtcompany.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public Mono<Payment> processPayment(Payment payment) {
        // Simulación de integración con pasarela de pagos Visa
        payment.setStatus("SUCCESS");
        return paymentRepository.save(payment);
    }

    public Mono<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    public Mono<Payment> refundPayment(Long id) {
        return paymentRepository.findById(id)
                .flatMap(payment -> {
                    // Lógica de reembolso
                    payment.setStatus("REFUNDED");
                    return paymentRepository.save(payment);
                });
    }
}