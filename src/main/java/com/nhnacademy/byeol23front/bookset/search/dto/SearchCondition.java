package com.nhnacademy.byeol23front.bookset.search.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchCondition {
    private String query;
    private String pathId;
    private SortOption sort;

    enum SortOption {
        POPULAR,
        NEWEST,
        LOW_PRICE,
        HIGH_PRICE,
        RATING,
        REVIEW
    }
}
