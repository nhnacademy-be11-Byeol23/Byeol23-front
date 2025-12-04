package com.nhnacademy.byeol23front.orderset.order.controller;

import java.math.BigDecimal;
import java.util.*;

import com.nhnacademy.byeol23front.couponset.coupon.client.CouponApiClient;
import com.nhnacademy.byeol23front.couponset.coupon.dto.OrderItemRequest;
import com.nhnacademy.byeol23front.couponset.coupon.dto.UsableCouponInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nhnacademy.byeol23front.bookset.book.client.BookApiClient;
import com.nhnacademy.byeol23front.bookset.book.dto.BookOrderRequest;
import com.nhnacademy.byeol23front.cartset.cart.dto.CartOrderRequest;
import com.nhnacademy.byeol23front.memberset.member.client.MemberApiClient;
import com.nhnacademy.byeol23front.memberset.member.dto.MemberMyPageResponse;
import com.nhnacademy.byeol23front.orderset.order.client.OrderApiClient;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderCancelRequest;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderCancelResponse;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderPrepareRequest;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderPrepareResponse;
import com.nhnacademy.byeol23front.orderset.order.dto.PointOrderResponse;
import com.nhnacademy.byeol23front.orderset.order.exception.OrderPrepareFailException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderApiClient orderApiClient;
    private final OrderUtil orderUtil;
    private final BookApiClient bookApiClient;
    private final MemberApiClient memberApiClient;
    private final CouponApiClient couponApiClient;

    @Value("${tossPayment.client-key}")
    private String tossClientKey;

    @PostMapping
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleOrderRequest(@CookieValue(name = "Access-Token", required = false) String token,
                                                                  @CookieValue(name= "guestId", required = false) String guestId,
                                                                  @RequestBody CartOrderRequest orderRequest) {

        if (Objects.isNull(token) || token.isEmpty()) {
            orderApiClient.saveGuestOrder(guestId, orderRequest);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("redirectUrl", "/orders");
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    @GetMapping
    public String getOrderForm(@RequestParam List<Long> bookIds,
                               @RequestParam List<Integer> quantities,
                               Model model) {

        MemberMyPageResponse member = memberApiClient.getMember().getBody();

        CartOrderRequest cartOrderRequest = orderUtil.createOrderRequest(bookIds, quantities);
        BookOrderRequest bookOrderRequest = bookApiClient.getBookOrder(cartOrderRequest).getBody();

        //쿠폰 사용을 위한 로직
        List<OrderItemRequest> orderItemRequests = new ArrayList<>();
        if (bookIds != null && quantities != null && bookIds.size() == quantities.size()) {
            for (int i = 0; i < bookIds.size(); i++) {
                // 리스트의 각 요소를 꺼내 DTO로 변환
                orderItemRequests.add(new OrderItemRequest(bookIds.get(i), quantities.get(i)));
            }
        }

        List<UsableCouponInfoResponse> usableCoupons = List.of(); // 기본값: 빈 리스트
        try {
            ResponseEntity<List<UsableCouponInfoResponse>> couponResponse =
                    couponApiClient.getUsableCoupons(orderItemRequests);

            if (couponResponse != null && couponResponse.getBody() != null) {
                usableCoupons = couponResponse.getBody();
                log.info("조회된 사용 가능 쿠폰 수: {}", usableCoupons.size());
            }
        } catch (Exception e) {
            log.error("쿠폰 목록 조회 중 오류 발생 (주문은 계속 진행): ", e);
        }

        orderUtil.addDeliveryDatesToModel(model);
        orderUtil.addDeliveryFeeToModel(model, bookOrderRequest);
        orderUtil.addOrderSummary(model, bookOrderRequest);
        orderUtil.addTotalQuantity(model, bookOrderRequest.bookList());
        orderUtil.addPackagingOption(model);

        model.addAttribute("defaultAddress", member.address());

        model.addAttribute("userPoint", member.currentPoint());

        model.addAttribute("clientKey", tossClientKey);

        model.addAttribute("usableCoupons", usableCoupons);

        return "order/checkout";
    }

    @PostMapping("/prepare")
    @ResponseBody
    public ResponseEntity<OrderPrepareResponse> prepareOrder(@Valid @RequestBody OrderPrepareRequest request,
                                                             @CookieValue(name = "Access-Token", required = false) String accessToken) {
        ResponseEntity<OrderPrepareResponse> response = orderApiClient.prepareOrder(request, accessToken);

        log.info("주문 준비 응답: {}", response.getBody());

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("주문 준비 실패: {}", response.getStatusCode());
            throw new OrderPrepareFailException("주문 임시 저장에 실패했습니다.");
        }

        return response;
    }

    @GetMapping("/success")
    public String getOrderWithPoints(@RequestParam String orderNumber,
                                     Model model) {
        ResponseEntity<PointOrderResponse> responseEntity = orderApiClient.saveOrderWithPoints(orderNumber);
        PointOrderResponse savedPaymentInfo = responseEntity.getBody();

        if (savedPaymentInfo == null) {
            log.error("백엔드에서 포인트 결제 정보를 받아오지 못했습니다. orderNumber: {}", orderNumber);
            model.addAttribute("status", 500);
            model.addAttribute("error", "Backend Response Error");
            model.addAttribute("message", "포인트 결제 내역 저장 후 정보를 받아오지 못했습니다.");
            return "error";
        }

        Map<String, Object> paymentInfo = new HashMap<>();
        paymentInfo.put("orderId", savedPaymentInfo.orderNumber());
        paymentInfo.put("totalAmount", BigDecimal.valueOf(savedPaymentInfo.totalAmount().longValue()));
        paymentInfo.put("method", savedPaymentInfo.method());

        model.addAttribute("orderId", savedPaymentInfo.orderNumber());
        model.addAttribute("paymentInfo", paymentInfo);

        return "order/success";
    }

    @PostMapping("/{order-number}/cancel")
    @ResponseBody
    public ResponseEntity<OrderCancelResponse> cancelOrder(@PathVariable(name = "order-number") String orderNumber,
                                                           @RequestBody OrderCancelRequest request) {

        OrderCancelResponse response = orderApiClient.cancelOrder(orderNumber, request).getBody();

        return ResponseEntity.ok(response);
    }



}
