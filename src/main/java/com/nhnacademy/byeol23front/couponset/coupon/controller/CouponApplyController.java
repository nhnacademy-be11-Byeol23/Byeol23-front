package com.nhnacademy.byeol23front.couponset.coupon.controller;

import com.nhnacademy.byeol23front.couponset.coupon.client.CouponApiClient;
import com.nhnacademy.byeol23front.couponset.coupon.dto.CouponApplyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController // JSON 응답을 위해 @RestController 사용
@RequestMapping("/api/coupon") // 💡 JS에서 요청하는 경로와 동일하게 설정
@RequiredArgsConstructor
public class CouponApplyController {

    private final CouponApiClient couponApiClient; // 백엔드 서비스와 통신하는 Feign Client

    /**
     * 프론트엔드 JS에서 호출하는 경로(/api/coupon/calculate-discount)를 받아서
     * 백엔드 서비스로 요청을 대리하고 결과를 반환합니다.
     */
    @PostMapping("/calculate-discount")
    public ResponseEntity<Map<String, Long>> calculateDiscountProxy(
            @RequestBody CouponApplyRequest request) {

        // 1. Feign Client를 통해 실제 백엔드 서비스로 요청 전달
        //    (백엔드 컨트롤러가 Map<String, Long>을 반환해야 함)
        // Feign Client 호출

        // 2. 백엔드 응답을 그대로 프론트엔드 JS로 전달
        return couponApiClient.calculateDiscount(request);
    }
}