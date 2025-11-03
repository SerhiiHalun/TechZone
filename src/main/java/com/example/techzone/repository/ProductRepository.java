package com.example.techzone.repository;


import com.example.techzone.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findById(long id);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByDiscount(int discount);
    List<Product> findByDiscountGreaterThan(int discount);

    List<Product> findByCategoryId(int categoryId);
}
