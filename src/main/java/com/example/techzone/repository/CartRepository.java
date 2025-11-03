package com.example.techzone.repository;


import com.example.techzone.model.Cart;
import com.example.techzone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    List<Cart> findAllCartsByUser(User user);
    Cart findCartByUser(User user);
}
