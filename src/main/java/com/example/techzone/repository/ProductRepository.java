package com.example.techzone.repository;


import com.example.techzone.dto.ProductCardDto;
import com.example.techzone.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"images"})
    Optional<Product> findById(Long id);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByDiscount(int discount);
    List<Product> findByDiscountGreaterThan(int discount);
    List<Product> findByCategoryId(int categoryId);
    @Query("SELECT new com.example.techzone.dto.ProductCardDto(" +
            "p.id, p.name, p.price, p.discount, " +
            "(SELECT i.imgUrl FROM Image i WHERE i.product = p AND i.isMain = true ORDER BY i.id LIMIT 1), " + // Подзапрос для картинки
            "AVG(f.rating), COUNT(f)) " +
            "FROM Product p " +
            "LEFT JOIN p.feedbackList f " +
            "GROUP BY p.id, p.name, p.price, p.discount")
    List<ProductCardDto> findAllProductsWithStats(Pageable pageable);

    @Query("SELECT new com.example.techzone.dto.ProductCardDto(" +
            "p.id, p.name, p.price, p.discount, " +
            "(SELECT i.imgUrl FROM Image i WHERE i.product = p AND i.isMain = true ORDER BY i.id LIMIT 1), " +
            "AVG(f.rating), COUNT(f)) " +
            "FROM Product p " +
            "LEFT JOIN p.feedbackList f " +
            "WHERE p.discount > 0 " +
            "GROUP BY p.id, p.name, p.price, p.discount")
    List<ProductCardDto> findDiscountedProductsWithStats(Pageable pageable);
}
