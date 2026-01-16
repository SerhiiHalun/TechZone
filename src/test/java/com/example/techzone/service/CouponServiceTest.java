package com.example.techzone.service;

import com.example.techzone.model.Coupon;

import com.example.techzone.repository.CouponRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {
    @InjectMocks
    private CouponService couponService;

    @Mock
    private CouponRepository couponRepository;
    @Test
    void validateCoupon_Success() {

        String code = "SALE20";
        Coupon coupon = new Coupon();
        coupon.setName(code);
        coupon.setExpirationDate(LocalDate.now().plusDays(1));
        coupon.setUsageLimit(100);
        coupon.setUsageCount(5);

        when(couponRepository.findByName(code)).thenReturn(Optional.of(coupon));

        Coupon result = couponService.validateCoupon(code);

        assertNotNull(result);
        assertEquals(code, result.getName());
    }
    @Test
    void validateCoupon_Throws_WhenExpired() {
        String code = "OLD";
        Coupon coupon = new Coupon();
        coupon.setName(code);
        coupon.setExpirationDate(LocalDate.now().minusDays(1));
        when(couponRepository.findByName(code)).thenReturn(Optional.of(coupon));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            couponService.validateCoupon(code);
        });

        assertEquals("Coupon has expired", exception.getMessage());
    }
    @Test
    void validateCoupon_Throws_WhenLimitReached() {
        String code = "LIMITED";
        Coupon coupon = new Coupon();
        coupon.setName(code);
        coupon.setExpirationDate(LocalDate.now().plusDays(1));
        coupon.setUsageLimit(10);
        coupon.setUsageCount(10);

        when(couponRepository.findByName(code)).thenReturn(Optional.of(coupon));

        assertThrows(IllegalArgumentException.class, () -> {
            couponService.validateCoupon(code);
        });
    }
    @Test
    void validateCoupon_Throws_WhenNotFound() {
        String code = "FAKE";
        when(couponRepository.findByName(code)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            couponService.validateCoupon(code);
        });
    }
    @Test
    void validateCoupon_IgnoreCase_Success() {
        String codeInput = "sale20";
        String codeStored = "SALE20";

        Coupon coupon = new Coupon();
        coupon.setName(codeStored);
        coupon.setExpirationDate(LocalDate.now().plusDays(1));

        when(couponRepository.findByName(codeStored)).thenReturn(Optional.of(coupon));

        Coupon result = couponService.validateCoupon(codeInput);

        assertNotNull(result);
        assertEquals(codeStored, result.getName());
    }
    @Test
    void increaseUsageCount_ShouldIncrementAndSave() {
        Coupon coupon = new Coupon();
        coupon.setUsageCount(5);

        couponService.increaseUsageCount(coupon);

        assertEquals(6, coupon.getUsageCount());
        verify(couponRepository).save(coupon);
    }
}