package com.project.shopapp.controller;

import com.project.shopapp.response.CouponCalculationResponse;
import com.project.shopapp.services.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/calculate")
    public ResponseEntity<?> caculatedCouponValue(
            @RequestParam("coupon_code") String couponCode,
            @RequestParam("total_amount") double totalAmount
    ) {
        try {
            double finalAmount = couponService.calculateCouponValue(couponCode, totalAmount);
            CouponCalculationResponse response = CouponCalculationResponse.builder()
                    .result(finalAmount)
                    .errorMessage("")
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(CouponCalculationResponse
                        .builder()
                            .result(totalAmount)
                            .errorMessage(e.getMessage())
                        .build());
        }
    }
}
