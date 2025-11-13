package com.nhnacademy.byeol23front.bookset.search.dto;

import java.time.LocalDate;
import java.util.List;

public record BookSearchResultResponse(
        String id,
        String title,
        List<String> author,
        String publisher,
        LocalDate publishedAt,
        int regularPrice,
        int salePrice,
        int reviewCount,
        float ratingAverage,
        boolean isSoldOut
) {}