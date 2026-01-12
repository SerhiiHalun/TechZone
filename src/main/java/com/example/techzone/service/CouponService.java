package com.example.techzone.service;

import com.example.techzone.model.Coupon;
import com.example.techzone.model.User;
import com.example.techzone.repository.CouponRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
    public Coupon createCoupon(String name, Integer discount, LocalDate expirationDate, Integer usageLimit) {
        Coupon coupon = new Coupon();
        coupon.setName(name.toUpperCase());
        coupon.setDiscount(discount);
        coupon.setExpirationDate(expirationDate);
        coupon.setUsageLimit(usageLimit);
        coupon.setUsageCount(0);
        return couponRepository.save(coupon);
    }
    public Coupon validateCoupon(String code) {

        Coupon coupon = couponRepository.findByName(code.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found"));

        if (coupon.getExpirationDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Coupon has expired");
        }

        if (coupon.getUsageLimit() != null && coupon.getUsageLimit() > 0) {
            if (coupon.getUsageCount() >= coupon.getUsageLimit()) {
                throw new IllegalArgumentException("Coupon usage limit reached");
            }
        }

        return coupon;
    }
    @Transactional
    public void increaseUsageCount(Coupon coupon) {
        coupon.setUsageCount(coupon.getUsageCount() + 1);
        couponRepository.save(coupon);
    }

    public void deleteCoupon(long id) {
        couponRepository.deleteById(id);
    }
}
