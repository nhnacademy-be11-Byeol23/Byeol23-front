package com.nhnacademy.byeol23front.couponset.coupon.controller;

import com.nhnacademy.byeol23front.couponset.couponpolicy.client.CouponPolicyApiClient;
import com.nhnacademy.byeol23front.couponset.couponpolicy.dto.CouponPolicyInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/coupon")
@RequiredArgsConstructor
public class CouponController {
    private final CouponPolicyApiClient couponPolicyApiClient;

    @GetMapping()
    public String couponIssuePage(Model model){
//        List<CouponPolicyInfoResponse> couponPolicyInfoResponseList
//                = couponPolicyApiClient.getCouponPolicies().getBody();
//        model.addAttribute("policies", couponPolicyInfoResponseList);

        return "admin/coupon/coupon_issue";
    }

    @PostMapping()
    public ResponseEntity<Void> issueCoupon(){



        return ResponseEntity.ok().build();
    }
}
