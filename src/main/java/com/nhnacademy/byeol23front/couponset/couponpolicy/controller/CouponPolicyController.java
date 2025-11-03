package com.nhnacademy.byeol23front.couponset.couponpolicy.controller;

import com.nhnacademy.byeol23front.couponset.couponpolicy.client.CouponPolicyApiClient;
import com.nhnacademy.byeol23front.couponset.couponpolicy.dto.CouponPolicyCreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/coupon-policy")
public class CouponPolicyController {
    //ApiClient
    private final CouponPolicyApiClient couponPolicyApiClient;

    @GetMapping("/create")
    public String categoryPage(Model model) {
        model.addAttribute("pageTitle", "쿠폰 정책 생성");

        return "admin/coupon/coupon_policy";
    }

    @PostMapping("/create")
    public String createCouponPolicy(CouponPolicyCreateRequest couponPolicyCreateRequest) {
        log.info(couponPolicyCreateRequest.toString());
        couponPolicyApiClient.couponPolicyCreate(couponPolicyCreateRequest);

        return "redirect:/admin/coupon/coupon_policy";
    }
}