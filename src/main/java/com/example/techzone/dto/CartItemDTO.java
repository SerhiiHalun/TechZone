package com.example.techzone.dto;

import com.example.techzone.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItemDTO {
    private Product product;
    private int amount;
}
