package com.nhnacademy.byeol23front.cartset.cart.controller;

import com.nhnacademy.byeol23front.cartset.cart.client.CartApiClient;
import com.nhnacademy.byeol23front.cartset.cart.dto.CartBookUpdateRequest;
import com.nhnacademy.byeol23front.cartset.cart.dto.CartResponse;
import com.nhnacademy.byeol23front.cartset.cart.service.GuestCartService;
import jakarta.servlet.http.HttpSession;
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
    private final GuestCartService guestCartService;

    // 장바구니 페이지 조회 (회원/비회원 통합)
    @GetMapping
    public String getCart(HttpSession session, Model model) {
        Long memberId = (Long) session.getAttribute("memberId");
        CartResponse cart;
        
        if (memberId != null) {
            // 회원: 백엔드 API 호출
            log.info("회원 장바구니 조회 - memberId: {}", memberId);
            cart = cartApiClient.getCartByMember(memberId);
        } else {
            // 비회원: Redis에서 조회
            String guestId = session.getId();
            log.info("비회원 장바구니 조회 - sessionId: {}", guestId);
            cart = guestCartService.getGuestCartWithBooks(guestId);
        }

        long totalPrice = 0L;
        if (cart.cartBooks() != null) {
            totalPrice = cart.cartBooks().stream()
                    .mapToLong(cb -> cb.salePrice().longValue() * cb.quantity())
                    .sum();
        }

        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("cart", cart);
        model.addAttribute("isMember", memberId != null);
        return "cart";
    }

    // 장바구니에 상품 추가 (회원/비회원 통합)
    @PostMapping("/items")
    @ResponseBody
    public ResponseEntity<Void> addToCart(
            HttpSession session,
            @RequestParam("bookId") Long bookId,
            @RequestParam(value = "quantity", defaultValue = "1") int quantity) {
        
        Long memberId = (Long) session.getAttribute("memberId");
        
        if (memberId != null) {
            // 회원: 백엔드 API 호출
            log.info("회원 장바구니 추가 - memberId: {}, bookId: {}, quantity: {}", memberId, bookId, quantity);
            // TODO: 회원용 장바구니 추가 API 호출 필요
            // cartApiClient.addCartBook(memberId, bookId, quantity);
        } else {
            // 비회원: Redis에 직접 저장
            String guestId = session.getId();
            log.info("비회원 장바구니 추가 - sessionId: {}, bookId: {}, quantity: {}", guestId, bookId, quantity);
            guestCartService.addCartItem(guestId, bookId, quantity);
        }
        
        return ResponseEntity.ok().build();
    }

    // 장바구니 상품 수량 수정 (회원/비회원 통합)
    @PutMapping("/items/{item-id}")
    @ResponseBody
    public ResponseEntity<Void> updateCartItem(
            HttpSession session,
            @PathVariable("item-id") Long itemId,
            @RequestParam("quantity") int quantity) {
        
        Long memberId = (Long) session.getAttribute("memberId");
        
        if (memberId != null) {
            // 회원: 백엔드 API 호출 (itemId = cartBookId)
            log.info("회원 장바구니 수량 수정 - memberId: {}, cartBookId: {}, quantity: {}", memberId, itemId, quantity);
            CartBookUpdateRequest request = new CartBookUpdateRequest(itemId, quantity);
            cartApiClient.updateCartBook(request);
        } else {
            // 비회원: Redis 업데이트 (itemId = bookId)
            String guestId = session.getId();
            log.info("비회원 장바구니 수량 수정 - sessionId: {}, bookId: {}, quantity: {}", guestId, itemId, quantity);
            guestCartService.updateCartItemQuantity(guestId, itemId, quantity);
        }
        
        return ResponseEntity.ok().build();
    }

    // 장바구니 상품 삭제 (회원/비회원 통합)
    @DeleteMapping("/items/{item-id}")
    @ResponseBody
    public ResponseEntity<Void> deleteCartItem(
            HttpSession session,
            @PathVariable("item-id") Long itemId) {
        
        Long memberId = (Long) session.getAttribute("memberId");
        
        if (memberId != null) {
            // 회원: 백엔드 API 호출 (itemId = cartBookId)
            log.info("회원 장바구니 삭제 - memberId: {}, cartBookId: {}", memberId, itemId);
            cartApiClient.deleteCartBook(itemId);
        } else {
            // 비회원: Redis에서 삭제 (itemId = bookId)
            String guestId = session.getId();
            log.info("비회원 장바구니 삭제 - sessionId: {}, bookId: {}", guestId, itemId);
            guestCartService.deleteCartItem(guestId, itemId);
        }
        
        return ResponseEntity.ok().build();
    }

}
