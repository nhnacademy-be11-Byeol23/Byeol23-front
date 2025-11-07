package com.nhnacademy.byeol23front.cartset.cart.controller;

import com.nhnacademy.byeol23front.cartset.cart.client.CartApiClient;
import com.nhnacademy.byeol23front.cartset.cart.dto.CartBookUpdateRequest;
import com.nhnacademy.byeol23front.cartset.cart.dto.CartResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartApiClient cartApiClient;

    // 장바구니 페이지 조회
    @GetMapping("/{member-id}")
    public String getCart(@PathVariable("member-id") Long memberId, Model model) {
        CartResponse cart = cartApiClient.getCartByMember(memberId);

        long totalPrice = 0L;
        if (cart.cartBooks() != null) {
            totalPrice = cart.cartBooks().stream()
                    .mapToLong(cb -> cb.salePrice().longValue() * cb.quantity())
                    .sum();
        }
        
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("cart", cart);
        return "cart";
    }

    // 장바구니 도서 수량 수정 (AJAX 요청용)
    @PutMapping("/cart-books")
    @ResponseBody
    public ResponseEntity<Void> updateCartBook(@RequestBody CartBookUpdateRequest request) {
        cartApiClient.updateCartBook(request);
        return ResponseEntity.ok().build();
    }

    // 장바구니 도서 삭제 (AJAX 요청용)
    @DeleteMapping("/cart-books/{cart-book-id}")
    @ResponseBody
    public ResponseEntity<Void> deleteCartBook(@PathVariable("cart-book-id") Long cartBookId) {
        cartApiClient.deleteCartBook(cartBookId);
        return ResponseEntity.ok().build();
    }

}
