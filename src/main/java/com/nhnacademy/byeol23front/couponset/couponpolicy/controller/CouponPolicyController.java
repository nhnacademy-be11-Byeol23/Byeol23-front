package com.nhnacademy.byeol23front.couponset.couponpolicy.controller;

import com.nhnacademy.byeol23front.bookset.book.client.BookApiClient;
import com.nhnacademy.byeol23front.bookset.book.dto.BookResponse;
import com.nhnacademy.byeol23front.bookset.category.client.CategoryApiClient;
import com.nhnacademy.byeol23front.bookset.category.dto.CategoryListResponse;
import com.nhnacademy.byeol23front.bookset.tag.dto.PageResponse;
import com.nhnacademy.byeol23front.couponset.couponpolicy.client.CouponPolicyApiClient;
import com.nhnacademy.byeol23front.couponset.couponpolicy.dto.CouponPolicyCreateRequest;
import com.nhnacademy.byeol23front.couponset.couponpolicy.dto.CouponPolicyInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier; // 💡 Qualifier import
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/coupon-policy")
public class CouponPolicyController {
    //ApiClient
    private final CouponPolicyApiClient couponPolicyApiClient;
    private final CategoryApiClient categoryApiClient;
    private final BookApiClient bookApiClient;

    @GetMapping
    public String couponPolicyPage(
            Model model,
            // 1. 쿠폰 정책 페이징: page, size 파라미터 사용 (크기 10)
            @PageableDefault(size = 10, sort = "couponPolicyId")
            @Qualifier("policy") Pageable policyPageable,

            // 2. 도서 목록 페이징: book_page, book_size 파라미터 사용 (크기 10)
            @PageableDefault(size = 10, page = 0, sort = "bookId")
            @Qualifier("book") Pageable bookPageable
    ) {
        // 1. 쿠폰 정책 목록 (페이징)
        ResponseEntity<Page<CouponPolicyInfoResponse>> policyResponse = couponPolicyApiClient.getCouponPolicies(policyPageable);
        model.addAttribute("pageTitle", "쿠폰 정책 생성");
        model.addAttribute("policies", policyResponse.getBody()); // 💡 .getBody() 호출

        // 2. 최상위 카테고리 정보
        List<CategoryListResponse> roots = categoryApiClient.getRoots();
        model.addAttribute("categories", roots);

        // 3. 도서 리스트 (페이징 적용)
        ResponseEntity<PageResponse<BookResponse>> bookResponse = bookApiClient.getBooks(
                bookPageable.getPageNumber(),
                bookPageable.getPageSize()
        );
        // 💡 books 대신 booksPage로 PageResponse 객체를 모델에 추가
        model.addAttribute("booksPage", bookResponse.getBody());

        // 💡 HTML에서 기존에 사용하던 "books" 모델을 위해 임시로 content만 추가 (Thymeleaf 수정의 번거로움을 줄이기 위해)
        model.addAttribute("books", bookResponse.getBody().content());

        return "admin/coupon/coupon_policy";
    }

    @PostMapping
    public String createCouponPolicy(CouponPolicyCreateRequest couponPolicyCreateRequest) {
        couponPolicyApiClient.couponPolicyCreate(couponPolicyCreateRequest);
        return "redirect:/admin/coupon-policy";
    }
}