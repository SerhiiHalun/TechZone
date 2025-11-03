package com.example.techzone.service;

import com.example.techzone.model.Coupon;
import com.example.techzone.model.User;
import com.example.techzone.repository.CouponRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    public Coupon getCouponById(int id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coupon not found"));
    }

    public Coupon createCoupon(String name, Long discount, User user, LocalDate expirationDate, boolean isUsed) {
        Coupon coupon = new Coupon();
        coupon.setName(name);
        coupon.setDiscount(discount);
        coupon.setUser(user);
        coupon.setExpirationDate(expirationDate);
        coupon.setUsed(isUsed);
        return couponRepository.save(coupon);
    }

    public Coupon updateCoupon(int id, String name, Long discount, User user, LocalDate expirationDate, Boolean isUsed) {
        return couponRepository.findById(id).map(coupon -> {
            if (name != null && !name.isBlank()) {
                coupon.setName(name);
            }
            if (discount != null) {
                coupon.setDiscount(discount);
            }
            if (user != null) {
                coupon.setUser(user);
            }
            if (expirationDate != null) {
                coupon.setExpirationDate(expirationDate);
            }
            if (isUsed != null) {
                coupon.setUsed(isUsed);
            }
            return couponRepository.save(coupon);
        }).orElseThrow(() -> new RuntimeException("Coupon not found"));
    }

    public void deleteCoupon(int id) {
        couponRepository.deleteById(id);
    }
}
