package com.nhnacademy.byeol23front.couponset.couponpolicy.controller;

import com.nhnacademy.byeol23front.couponset.couponpolicy.dto.CouponPolicyCreateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/coupon-policy")
public class CouponPolicyController {
    @GetMapping("")
    public String couponPolicyPage(){

        return "admin/management";
    }

    @PostMapping("/create")
    public ResponseEntity createCouponPolicy(@RequestBody CouponPolicyCreateRequest couponPolicyCreateRequest){
        log.info(couponPolicyCreateRequest.toString());

        return ResponseEntity.ok().build();
    }
}