package com.nhnacademy.byeol23front.bookset.contributor.dto;

public record ContributorFindOrCreateRequest(
	String contributorName,
	String contributorRole
) {
}
