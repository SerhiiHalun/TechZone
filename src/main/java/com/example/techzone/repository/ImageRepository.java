package com.example.techzone.repository;


import com.example.techzone.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image,Long> {
    @Modifying
    @Query("UPDATE Image i SET i.isMain = false WHERE i.product.id = :productId")
    void updateIsMainFalseForProduct(@Param("productId") int productId);
    List<Image> findAllByProduct_Id(int productId);
    List<Image> findAllByIsMainTrue();
}
