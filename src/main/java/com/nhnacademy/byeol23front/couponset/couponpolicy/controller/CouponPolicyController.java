package com.nhnacademy.byeol23front.couponset.couponpolicy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/coupon-policy")
public class CouponPolicyController {
    @GetMapping("")
    public String couponPolicyPage(){

        return "admin-management/admin-management";
    }
}
