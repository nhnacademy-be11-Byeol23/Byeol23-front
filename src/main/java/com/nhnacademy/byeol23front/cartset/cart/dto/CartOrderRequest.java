package com.nhnacademy.byeol23front.cartset.cart.dto;

import com.nhnacademy.byeol23front.bookset.contributor.dto.AllContributorResponse;
import com.nhnacademy.byeol23front.bookset.publisher.dto.AllPublishersInfoResponse;

import java.math.BigDecimal;
import java.util.List;

public record CartOrderRequest(
        Long bookId,
        String bookName,
        String imageUrl,
        boolean isPack,
        BigDecimal regularPrice,
        BigDecimal salePrice,
        AllPublishersInfoResponse publisher,
        int quantity,
        List<AllContributorResponse> contributors,
        Long packagingId
) {}
