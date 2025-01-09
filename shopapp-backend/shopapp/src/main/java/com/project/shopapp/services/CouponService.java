package com.project.shopapp.services;

public interface CouponService {
    double calculateCouponValue(String couponCode, double totalAmount);
}
