package com.nhnacademy.byeol23front.memberset.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {
    
    @GetMapping
    public String getMypage(Model model) {
        model.addAttribute("activeTab", "orders");
        return "mypage/orders";
    }
    
    @GetMapping("/orders")
    public String getOrders(Model model) {
        model.addAttribute("activeTab", "orders");
        return "mypage/orders";
    }
    
    @GetMapping("/wishlist")
    public String getWishlist(Model model) {
        model.addAttribute("activeTab", "wishlist");
        return "mypage/wishlist";
    }
    
    @GetMapping("/wallet")
    public String getWallet(Model model) {
        model.addAttribute("activeTab", "wallet");
        return "mypage/coupons";
    }
    
    @GetMapping("/reviews")
    public String getReviews(Model model) {
        model.addAttribute("activeTab", "reviews");
        return "mypage/reviews";
    }
    
    @GetMapping("/addresses")
    public String getAddresses(Model model) {
        model.addAttribute("activeTab", "addresses");
        return "mypage/address";
    }
    
    @GetMapping("/settings")
    public String getSettings(Model model) {
        model.addAttribute("activeTab", "settings");
        return "mypage/settings";
    }
}
