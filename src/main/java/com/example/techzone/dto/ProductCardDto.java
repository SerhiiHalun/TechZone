package com.example.techzone.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCardDto {
    private Long id;
    private String name;
    private double price;
    private double discount;
    private String imgUrl;
    private Double averageRating;
    private Long reviewCount;

    public double getFinalPrice() {
        if (discount == 0) return price;
        return Math.round(price * (1 - discount / 100.0) * 100.0) / 100.0;
    }

    public int getIntRating() {
        return averageRating == null ? 0 : (int) Math.round(averageRating);
    }
}