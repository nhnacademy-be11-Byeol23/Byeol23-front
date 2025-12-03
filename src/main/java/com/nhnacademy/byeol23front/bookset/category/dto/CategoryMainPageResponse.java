package com.nhnacademy.byeol23front.bookset.category.dto;

public record CategoryMainPageResponse(
	Long id, String categoryName, String pathId, Long categoryBookCount, String repImgUrl
) {
}
