package com.shirtcompany.payment.controller;

import com.shirtcompany.payment.model.Payment;
import com.shirtcompany.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/process")
    public Mono<Payment> processPayment(@RequestBody Payment payment) {
        return paymentService.processPayment(payment);
    }

    @GetMapping("/{id}")
    public Mono<Payment> getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }

    @PostMapping("/{id}/refund")
    public Mono<Payment> refundPayment(@PathVariable Long id) {
        return paymentService.refundPayment(id);
    }
}