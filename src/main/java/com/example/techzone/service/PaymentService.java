package com.example.techzone.service;


import com.example.techzone.model.Order;
import com.example.techzone.model.Payment;
import com.example.techzone.repository.OrderRepository;
import com.example.techzone.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    public String createPaymentIntent(Order order) throws StripeException {
        Stripe.apiKey = stripeApiKey;

        long amountInCents = (long) (order.getTotalAmount() * 100);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("usd")
                .setDescription("Order #" + order.getId() + " for " + order.getUser().getFirstName())
                .putMetadata("order_id", order.getId().toString())
                .build();

        PaymentIntent intent = PaymentIntent.create(params);


        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod("STRIPE");
        payment.setAmount(order.getTotalAmount());
        payment.setStatus(Payment.Status.PENDING);
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);

        return intent.getClientSecret();
    }

    @Transactional
    public void updatePaymentStatus(Long orderId, boolean success) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(success ? Order.Status.PAID : Order.Status.CANCELLED);
        orderRepository.save(order);

        Payment payment = order.getPayment();
        if (payment != null) {
            payment.setStatus(success ? Payment.Status.SUCCESS : Payment.Status.FAILED);
            paymentRepository.save(payment);
        }
    }


}

