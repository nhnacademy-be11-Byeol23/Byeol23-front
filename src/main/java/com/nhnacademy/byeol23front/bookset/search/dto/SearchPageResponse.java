package com.nhnacademy.byeol23front.bookset.search.dto;

import java.util.List;

public record SearchPageResponse<T>(int page, int size, long totalElements, int totalPages, List<T> content) {
}
