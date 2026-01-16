package com.example.techzone.service;

import com.example.techzone.dto.CartItemDTO;
import com.example.techzone.model.Coupon;
import com.example.techzone.model.Product;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionCartServiceTest {
    @InjectMocks
    private SessionCartService sessionCartService;
    @Mock
    private HttpSession session;
    @Mock
    private ProductService productService;
    @Test
    void testCalculateGrandTotal_NoDiscounts_NoCoupon() {

        Product product = new Product();
        product.setPrice(100.0);
        product.setDiscount(0L);
        List<CartItemDTO> cart = List.of(new CartItemDTO(product, 2));
        when(session.getAttribute("cart")).thenReturn(cart);
        when(session.getAttribute("appliedCoupon")).thenReturn(null);
        double result = sessionCartService.calculateGrandTotal(session);
        assertEquals(200.0, result, "Total should be sum of products without discounts");
    }
    @Test
    void testCalculateGrandTotal_WithCoupon() {
        Product product = new Product();
        product.setPrice(100.0);
        product.setDiscount(0L);
        List<CartItemDTO> cart = List.of(new CartItemDTO(product,1));
        Coupon coupon = new Coupon();
        coupon.setDiscount(10);
        when(session.getAttribute("cart")).thenReturn(cart);
        when(session.getAttribute("appliedCoupon")).thenReturn(coupon);
        double result = sessionCartService.calculateGrandTotal(session);
        assertEquals(90.0, result, "Price must be reduce");
    }
    @Test
    void testCalculateGrandTotal_WithProductDiscountAndCoupon() {
        Product product = new Product();
        product.setPrice(100.0);
        product.setDiscount(10L);

        List<CartItemDTO> cart = List.of(new CartItemDTO(product, 1));

        Coupon coupon = new Coupon();
        coupon.setDiscount(20);
        when(session.getAttribute("cart")).thenReturn(cart);
        when(session.getAttribute("appliedCoupon")).thenReturn(coupon);

        double result = sessionCartService.calculateGrandTotal(session);


        assertEquals(72.0, result, "Discounts should be applied consistently");
    }
    @Test
    void testUpdateAmount_RemovesItem_WhenAmountBecomesZero() {

        long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setAvailAmount(100);

        List<CartItemDTO> cart = new ArrayList<>();
        cart.add(new CartItemDTO(product, 1));
        when(session.getAttribute("cart")).thenReturn(cart);
        when(productService.getProductById(productId)).thenReturn(product);
        sessionCartService.updateAmount(productId, -1, session);

        assertEquals(0, cart.size(), "The item must be removed from the basket");
    }
    @Test
    void testUpdateAmount_MaxLimitReached() {
        long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setAvailAmount(100);
        List<CartItemDTO> cart = new ArrayList<>();
        CartItemDTO item = new CartItemDTO(product, 10);
        cart.add(item);

        when(session.getAttribute("cart")).thenReturn(cart);
        when(productService.getProductById(productId)).thenReturn(product);
        when(productService.hasEnoughStock(eq(productId), anyInt())).thenReturn(true);

        sessionCartService.updateAmount(productId, 1, session);

        assertEquals(10, item.getAmount(), "Quantity should not exceed 10");
    }
    @Test
    void testUpdateAmount_NotEnoughStock() {
        long productId = 1L;
        Product product = new Product();
        product.setId(productId);

        List<CartItemDTO> cart = new ArrayList<>();
        CartItemDTO item = new CartItemDTO(product, 5);
        cart.add(item);

        when(session.getAttribute("cart")).thenReturn(cart);
        when(productService.getProductById(productId)).thenReturn(product);

        when(productService.hasEnoughStock(eq(productId), anyInt())).thenReturn(false);

        sessionCartService.updateAmount(productId, 1, session);

        assertEquals(5, item.getAmount(), "Should not increase amount if stock is low");
    }
}