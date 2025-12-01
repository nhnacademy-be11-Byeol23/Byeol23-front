package com.nhnacademy.byeol23front.cartset.cart.controller;

import com.nhnacademy.byeol23front.cartset.cart.client.CartApiClient;
import com.nhnacademy.byeol23front.cartset.cart.dto.CartBookAddRequest;
import com.nhnacademy.byeol23front.cartset.cart.dto.CartBookResponse;
import com.nhnacademy.byeol23front.cartset.cart.dto.CartBookUpdateRequest;
import com.nhnacademy.byeol23front.cartset.cart.dto.FreeShippingCondition;
import com.nhnacademy.byeol23front.orderset.delivery.client.DeliveryApiClient;
import com.nhnacademy.byeol23front.orderset.delivery.dto.DeliveryPolicyInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CartController {
    private final CartApiClient cartApiClient;
    private final DeliveryApiClient deliveryApiClient;

    @GetMapping("/carts/books")
    public String cartPage(Model model) throws InterruptedException, ExecutionException {
        try(var taskScope = new StructuredTaskScope.ShutdownOnFailure()) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            var cartTask = taskScope.fork(() -> {
                RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(attributes.getRequest()));
                return cartApiClient.getCartBooks();
            });
            var deliveryTask = taskScope.fork(() -> {
                RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(attributes.getRequest()));
                return deliveryApiClient.getCurrentDeliveryPolicy();
            });

            taskScope.join();
            taskScope.throwIfFailed();

            List<CartBookResponse> cartBooks = cartTask.get();
            ResponseEntity<DeliveryPolicyInfoResponse> currentDeliveryPolicy = deliveryTask.get();
            DeliveryPolicyInfoResponse deliveryPolicy = currentDeliveryPolicy.getBody();
            FreeShippingCondition freeShippingCondition = new FreeShippingCondition(deliveryPolicy.freeDeliveryCondition().intValue(), deliveryPolicy.deliveryFee().intValue());

            model.addAttribute("cartBooks", cartBooks);
            model.addAttribute("shippingCondition", freeShippingCondition);
            return "cart/carts";
        }
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
