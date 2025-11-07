package com.nhnacademy.byeol23front.bookset.publisher.dto;

import java.util.List;

public record PageResponse<T>(
	List<T> content,
	int number,
	int size,
	long totalElements,
	int totalPages,
	boolean first,
	boolean last
) {
}
