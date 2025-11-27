package com.nhnacademy.byeol23front.cartset.cart.controller;

import com.nhnacademy.byeol23front.cartset.cart.client.CartApiClient;
import com.nhnacademy.byeol23front.cartset.cart.dto.CartBookAddRequest;
import com.nhnacademy.byeol23front.cartset.cart.dto.CartBookResponse;
import com.nhnacademy.byeol23front.cartset.cart.dto.CartBookUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {
    private final CartApiClient cartApiClient;

    @GetMapping("/carts/books")
    public String cartPage(Model model) {
        List<CartBookResponse> cartBooks = cartApiClient.getCartBooks();
        model.addAttribute("cartBooks", cartBooks);
        return "cart/carts";
    }

    @PostMapping("/carts/books")
    @ResponseBody
    public void addCartBook(@RequestBody CartBookAddRequest request) {
        cartApiClient.addCartBook(request);
    }

    @PostMapping("/carts/books/{book-id}/update")
    @ResponseBody
    public void updateCartBook(@PathVariable("book-id") Long bookId, @RequestBody CartBookUpdateRequest request) {
        cartApiClient.updateCartBook(bookId, request);
    }

    @PostMapping("/carts/books/{book-id}/delete")
    @ResponseBody
    public void deleteCartBook(@PathVariable("book-id") Long bookId) {
        cartApiClient.deleteCartBook(bookId);
    }
}
