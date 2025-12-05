package com.nhnacademy.byeol23front.orderset.order.controller;

import java.math.BigDecimal;
import java.util.*;

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
import com.nhnacademy.byeol23front.bookset.book.dto.BookInfoRequest;
import com.nhnacademy.byeol23front.bookset.book.dto.BookOrderRequest;
import com.nhnacademy.byeol23front.bookset.book.dto.BookResponse;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderRequest;
import com.nhnacademy.byeol23front.couponset.coupon.client.CouponApiClient;
import com.nhnacademy.byeol23front.memberset.member.client.MemberApiClient;
import com.nhnacademy.byeol23front.memberset.member.dto.MemberMyPageResponse;
import com.nhnacademy.byeol23front.orderset.order.client.OrderApiClient;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderCancelRequest;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderCancelResponse;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderPrepareRequest;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderPrepareResponse;
import com.nhnacademy.byeol23front.orderset.order.dto.PointOrderResponse;
import com.nhnacademy.byeol23front.orderset.order.exception.OrderPrepareFailException;

import feign.FeignException;
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
    public ResponseEntity<Map<String, String>> handleOrderRequest(
            @CookieValue(name = "Access-Token", required = false) String token,
            @CookieValue(name = "guestId", required = false) String guestId,
            @RequestBody OrderRequest orderRequest) {

        String validationToken;
        final String REDIRECT_URL = "/orders";

        if (Objects.isNull(token) || token.isEmpty()) {
            try {
                ResponseEntity<Map<String, Object>> response = orderApiClient.saveGuestOrder(guestId, orderRequest);
                validationToken = (String)response.getBody().get("validationToken");

                Map<String, String> responseBody = new HashMap<>();
                responseBody.put("validationToken", validationToken);
                responseBody.put("redirectUrl", REDIRECT_URL);

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
            } catch (FeignException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "주문 임시 저장 중 API 통신 오류 발생"));
            }

        }

        ResponseEntity<Map<String, Object>> response = orderApiClient.saveMemberOrderTmp(orderRequest);
        validationToken = (String)response.getBody().get("validationToken");

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("validationToken", validationToken);
        responseBody.put("redirectUrl", REDIRECT_URL);

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    //장바구니에서 주문
    @GetMapping
    public String getOrderForm(@RequestParam("token") String validationToken,
                               Model model) {
        MemberMyPageResponse member = memberApiClient.getMember();

        OrderRequest orderRequest = orderApiClient.getAndRemoveOrderRequest(validationToken);
        BookOrderRequest bookOrderRequest = bookApiClient.getBookOrder(orderRequest).getBody();

        //쿠폰로직
        List<OrderItemRequest> orderItemRequests = new ArrayList<>();

        if (orderRequest.orderList() != null && !orderRequest.orderList().isEmpty()) {
            // Map<Long, Integer> -> List<OrderItemRequest> 변환
            orderItemRequests = orderRequest.orderList().entrySet().stream()
                    .map(entry -> new OrderItemRequest(
                            entry.getKey(),   // Map Key -> bookId
                            entry.getValue()  // Map Value -> quantity
                    ))
                    .toList();
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

    @PostMapping("/direct-check")
    @ResponseBody
    public ResponseEntity<Void> handleDirectOrderCheck(
            @CookieValue(name = "Access-Token", required = false) String token) {

        if (Objects.isNull(token) || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    //바로구매
    @GetMapping("/direct")
    public String getOrderFormDirect(
            @RequestParam Long bookId,
            @RequestParam int quantity,
            Model model) {

        BookResponse book = bookApiClient.getBook(bookId).getBody();

        MemberMyPageResponse member = memberApiClient.getMember();

        BookInfoRequest bookInfo = new BookInfoRequest(
                bookId, book.bookName(), book.images().getFirst().imageUrl(), // 이미지 null 체크는 Service에서 처리해야 안전함
                book.isPack(), book.regularPrice(), book.salePrice(), book.publisher(), quantity,
                book.contributors(), null
        );
        List<BookInfoRequest> bookOrderInfo = List.of(bookInfo);
        BookOrderRequest request = new BookOrderRequest(bookOrderInfo); // BookOrderRequest DTO 사용

        orderUtil.addTotalQuantity(model, request.bookList());
        orderUtil.addDeliveryDatesToModel(model);
        orderUtil.addOrderSummary(model, request);
        orderUtil.addDeliveryFeeToModel(model, request);
        orderUtil.addPackagingOption(model);

        model.addAttribute("defaultAddress", member.address());
        model.addAttribute("userPoint", member.currentPoint());
        model.addAttribute("clientKey", tossClientKey);

        return "order/checkout";
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
