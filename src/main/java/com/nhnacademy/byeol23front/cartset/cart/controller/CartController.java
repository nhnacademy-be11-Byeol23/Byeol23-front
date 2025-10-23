package com.nhnacademy.byeol23front.cartset.cart.controller;

import com.nhnacademy.byeol23front.common.ShopFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final ShopFeignClient feignClient;

    @GetMapping
    public String getCart() {return "cart"; }

}
