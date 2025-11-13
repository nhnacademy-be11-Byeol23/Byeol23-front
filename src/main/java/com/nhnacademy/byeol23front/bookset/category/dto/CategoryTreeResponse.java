package com.nhnacademy.byeol23front.bookset.category.dto;

import java.util.List;

public record CategoryTreeResponse(Long categoryId, String categoryName, String pathId, List<CategoryTreeResponse> children) {
}