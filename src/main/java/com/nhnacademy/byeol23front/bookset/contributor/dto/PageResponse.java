package com.nhnacademy.byeol23front.bookset.contributor.dto;

import java.util.List;

public record PageResponse<T>(
	List<T> content,
	int number,
	int size,
	boolean first,
	boolean last,
	int totalPages,
	long totalElements
) {
}
