package com.example.techzone.service;

import com.example.techzone.model.Order;
import com.example.techzone.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(int id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    public Order createOrder(LocalDate creationDate, double totalPrice, String status) {
        Order order = new Order();
        order.setCreationDate(creationDate);
        order.setTotalPrice(totalPrice);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public Order updateOrder(int id, LocalDate creationDate, Double totalPrice, String status) {
        return orderRepository.findById(id).map(order -> {
            if (creationDate != null) {
                order.setCreationDate(creationDate);
            }
            if (totalPrice != null) {
                order.setTotalPrice(totalPrice);
            }
            if (status != null && !status.isBlank()) {
                order.setStatus(status);
            }
            return orderRepository.save(order);
        }).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public void deleteOrder(int id) {
        orderRepository.deleteById(id);
    }
}
