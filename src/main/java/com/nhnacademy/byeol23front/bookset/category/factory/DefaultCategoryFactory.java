package com.nhnacademy.byeol23front.bookset.category.factory;

import com.nhnacademy.byeol23front.bookset.category.dto.CategoryMainPageResponse;

public class DefaultCategoryFactory {

	public static CategoryMainPageResponse createDefaultCategory(int index) {
		return new CategoryMainPageResponse(
			-1L,                       // dummy id
			"Coming soon " + (index+1),// name,
			null,
			0L,
			null
		);
	}
}
