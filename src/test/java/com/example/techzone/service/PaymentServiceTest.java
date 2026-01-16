package com.example.techzone.service;

import com.example.techzone.model.*;
import com.example.techzone.repository.OrderRepository;
import com.example.techzone.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private ProductService productService;
    @Mock
    private CouponService couponService;
    @Test
    void updatePaymentStatus_Success_ShouldDecreaseStockAndSaveOrder() {
        Long orderId = 1L;

        Product product = new Product();
        product.setId(10L);

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setAmount(2L);

        Order order = new Order();
        order.setId(orderId);
        order.setStatus(Order.Status.PENDING);
        order.setItems(List.of(item));

        Payment payment = new Payment();
        order.setPayment(payment);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        paymentService.updatePaymentStatus(orderId, true);
        assertEquals(Order.Status.PAID, order.getStatus());
        assertEquals(Payment.Status.SUCCESS, payment.getStatus());

        verify(productService, times(1)).decreaseStock(10L, 2L);
        verify(orderRepository, times(1)).save(order);
    }
    @Test
    void updatePaymentStatus_Failure_ShouldCancelOrderAndNotDecreaseStock() {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(Order.Status.PENDING);

        Payment payment = new Payment();
        order.setPayment(payment);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        paymentService.updatePaymentStatus(orderId, false);

        assertEquals(Order.Status.CANCELLED, order.getStatus());
        assertEquals(Payment.Status.FAILED, payment.getStatus());
        verify(productService, never()).decreaseStock(anyLong(), anyLong());
        verify(couponService, never()).increaseUsageCount(any());
        verify(orderRepository).save(order);
    }
    @Test
    void updatePaymentStatus_Success_WithCoupon_ShouldIncrementUsage() {
        Long orderId = 1L;
        String couponCode = "SALE20";

        Order order = new Order();
        order.setId(orderId);
        order.setCouponCode(couponCode);
        order.setStatus(Order.Status.PENDING);
        order.setPayment(new Payment());

        Coupon coupon = new Coupon();
        coupon.setName(couponCode);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(couponService.validateCoupon(couponCode)).thenReturn(coupon);

        paymentService.updatePaymentStatus(orderId, true);

        verify(couponService, times(1)).increaseUsageCount(coupon);
    }
}