package com.nhnacademy.byeol23front.bookset.book.dto;

import java.math.BigDecimal;
import java.util.List;

import com.nhnacademy.byeol23front.bookset.contributor.dto.AllContributorResponse;
import com.nhnacademy.byeol23front.bookset.publisher.dto.AllPublishersInfoResponse;

public record BookInfoRequest(Long bookId,
							  String bookName,
							  String imageUrl,
							  boolean isPack,
							  BigDecimal regularPrice,
							  BigDecimal salePrice,
							  AllPublishersInfoResponse publisher,
							  int quantity,
							  List<AllContributorResponse> contributors,
							  Long packagingId) {
}
