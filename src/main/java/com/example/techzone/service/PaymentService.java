package com.example.techzone.service;

import com.example.techzone.model.Payment;
import com.example.techzone.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(int id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
    }

//    public Payment createPayment(Order order, String paymentMethod, String status) {
//        Payment payment = new Payment();
//        payment.setOrder(order);
//        payment.setPaymentMethod(paymentMethod);
//        payment.setStatus(status);
//        payment.setPaymentDate(LocalDate.now());
//
//        return paymentRepository.save(payment);
//    }
//
//    public Payment updatePayment(int id, Order order, String paymentMethod, String status) {
//        return paymentRepository.findById(id).map(payment -> {
//            if (order != null) {
//                payment.setOrder(order);
//            }
//            if (paymentMethod != null) {
//                payment.setPaymentMethod(paymentMethod);
//            }
//            if (status != null) {
//                payment.setStatus(status);
//            }
//
//            return paymentRepository.save(payment);
//        }).orElseThrow(() -> new RuntimeException("Payment not found"));
//    }

    public void deletePayment(int id) {
        paymentRepository.deleteById(id);
    }
}
