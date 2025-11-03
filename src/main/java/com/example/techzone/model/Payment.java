package com.example.techzone.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

//    @ManyToOne
//    @JoinColumn(name = "order_id", nullable = false)
//    private Order order;

    @Column(nullable = false)
    private String paymentMethod;

    @Column(nullable = false)
    private String status;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;
}
