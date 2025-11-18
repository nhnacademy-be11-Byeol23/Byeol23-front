package com.nhnacademy.byeol23front.couponset.couponpolicy.controller;

import com.nhnacademy.byeol23front.bookset.category.client.CategoryApiClient;
import com.nhnacademy.byeol23front.bookset.category.dto.CategoryListResponse;
import com.nhnacademy.byeol23front.couponset.couponpolicy.client.CouponPolicyApiClient;
import com.nhnacademy.byeol23front.couponset.couponpolicy.dto.CouponPolicyCreateRequest;
import com.nhnacademy.byeol23front.couponset.couponpolicy.dto.CouponPolicyInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/coupon-policy")
public class CouponPolicyController {
    //ApiClient
    private final CouponPolicyApiClient couponPolicyApiClient;
    private final CategoryApiClient categoryApiClient;
    @GetMapping
    public String couponPolicyPage(Model model) {
        ResponseEntity<List<CouponPolicyInfoResponse>> response = couponPolicyApiClient.getCouponPolicies();
        model.addAttribute("pageTitle", "쿠폰 정책 생성");
        model.addAttribute("policies", response.getBody());

        //최상위 카테고리 정보
        List<CategoryListResponse> roots = categoryApiClient.getRoots();
        model.addAttribute("categories", roots);
        return "admin/coupon/coupon_policy";
    }

    @PostMapping
    public String createCouponPolicy(CouponPolicyCreateRequest couponPolicyCreateRequest) {
        log.info(couponPolicyCreateRequest.toString());
        couponPolicyApiClient.couponPolicyCreate(couponPolicyCreateRequest);

        return "redirect:/admin/coupon-policy";
    }
}