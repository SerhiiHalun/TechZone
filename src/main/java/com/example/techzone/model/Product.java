package com.example.techzone.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name",nullable = false)
    private String name;
    @Column(name = "description", nullable = false, columnDefinition = "text")
    private String description;
    @Column(name = "price", nullable = false)
    private double price;
    @Column(name = "avail_amount",nullable = false)
    private long availAmount;
    @Column(name = "discount")
    private long discount;
    @Column(name = "created_date", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Image> images;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Feedback> feedbackList;
    @Transient
    public double getFinalPrice() {
        return Math.round(price * (1 - discount / 100.0) * 100.0) / 100.0;
    }

}
