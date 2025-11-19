package com.nhnacademy.byeol23front.bookset.bookAladin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nhnacademy.byeol23front.bookset.book.client.BookApiClient;
import com.nhnacademy.byeol23front.bookset.book.dto.BookCreateRequest;
import com.nhnacademy.byeol23front.bookset.book.dto.BookResponse;
import com.nhnacademy.byeol23front.bookset.bookAladin.dto.AladinResult;
import com.nhnacademy.byeol23front.bookset.bookAladin.dto.BookAladinCreateRequest;
import com.nhnacademy.byeol23front.bookset.bookAladin.service.BookAladinService;
import com.nhnacademy.byeol23front.bookset.contributor.client.ContributorApiClient;
import com.nhnacademy.byeol23front.bookset.contributor.dto.AllContributorResponse;
import com.nhnacademy.byeol23front.bookset.contributor.dto.ContributorFindOrCreateRequest;
import com.nhnacademy.byeol23front.bookset.contributor.dto.ContributorRole;
import com.nhnacademy.byeol23front.bookset.publisher.client.PublisherApiClient;
import com.nhnacademy.byeol23front.bookset.publisher.dto.AllPublishersInfoResponse;
import com.nhnacademy.byeol23front.bookset.publisher.dto.PublisherCreateRequest;
import com.nhnacademy.byeol23front.minio.service.MinioService;
import com.nhnacademy.byeol23front.minio.util.ImageDomain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/bookApi")
public class BookAladinController {

	private final BookAladinService bookAladinService;
	private final BookApiClient bookApiClient;
	private final PublisherApiClient publisherApiClient;
	private final ContributorApiClient contributorApiClient;
	private final MinioService minioService;

	@GetMapping
	public String getAllBooks(@RequestParam(required = false) String keyword, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size, Model model) throws JsonProcessingException {
		AladinResult result = bookAladinService.getAllBooks(keyword, page, size);
		model.addAttribute("books", result.item());
		model.addAttribute("page", result.page());
		model.addAttribute("size", result.size());
		model.addAttribute("lastPage", result.lastPage());
		model.addAttribute("keyword", result.keyword());
		model.addAttribute("total", result.total());

		return "admin/bookAladin/bookAladin";
	}

	@GetMapping("/new")
	public String createBook(){
		return "admin/bookAladin/bookAladinCreate";
	}

	@PostMapping("/new")
	public String createBookFromAladin(@RequestBody BookAladinCreateRequest request) {
		try {
			log.info("알라딘 도서 생성 요청: {}", request);
			
			// 1. 출판사 찾기 또는 생성
			Long publisherId;
			if (request.publisher() != null && !request.publisher().isBlank()) {
				try {
					AllPublishersInfoResponse publisherResponse = publisherApiClient
						.findOrCreatePublisher(new PublisherCreateRequest(request.publisher()))
						.getBody();
					if (publisherResponse == null) {
						log.error("출판사 응답이 null입니다.");
						throw new IllegalArgumentException("출판사 조회/생성에 실패했습니다.");
					}
					publisherId = publisherResponse.publisherId();
					log.info("출판사 ID: {}", publisherId);
				} catch (Exception e) {
					log.error("출판사 찾기/생성 실패: {}", e.getMessage(), e);
					throw new IllegalArgumentException("출판사 처리 실패: " + e.getMessage());
				}
			} else {
				throw new IllegalArgumentException("출판사 이름이 필요합니다.");
			}

			// 2. 기여자 파싱 및 찾기 또는 생성
			List<Long> contributorIds = new ArrayList<>();
			log.info("저자 정보: {}",request.author());
			log.info("역자 정보: {}",request.translator());
			if (!request.author().isBlank()) {
				try {
					List<ContributorFindOrCreateRequest> parsedAuthors = parseContributors(request.author(), ContributorRole.AUTHOR);
					List<ContributorFindOrCreateRequest> parsedTranslators = parseContributors(request.translator(), ContributorRole.TRANSLATOR);

					contributorIds.addAll(findOrCreateContributors(parsedAuthors));
					contributorIds.addAll(findOrCreateContributors(parsedTranslators));
				} catch (Exception e) {
					log.error("기여자 파싱 실패: {}", e.getMessage());
					// 기여자 파싱 실패해도 계속 진행
				}
			}

			// 3. 필수 필드 검증
			if (request.bookName() == null || request.bookName().isBlank()) {
				throw new IllegalArgumentException("도서명이 필요합니다.");
			}
			if (request.isbn() == null || request.isbn().isBlank()) {
				throw new IllegalArgumentException("ISBN이 필요합니다.");
			}
			if (publisherId == null) {
				throw new IllegalArgumentException("출판사 ID가 필요합니다.");
			}

			// 4. 도서 생성
			BookCreateRequest bookRequest = new BookCreateRequest(
				request.bookName(),
				request.toc() != null ? request.toc() : "",  // null 체크
				request.description() != null ? request.description() : "",  // null 체크
				request.regularPrice(),
				request.salePrice(),
				request.isbn(),
				request.publishDate(),
				request.isPack(),
				request.bookStatus(),  // 기본값
				request.stock() != null ? request.stock() : 0,  // 기본값
				publisherId,
				request.categoryIds() != null ? request.categoryIds() : List.of(),
				request.tagIds() != null ? request.tagIds() : List.of(),
				contributorIds
			);

			log.info("도서 생성 요청: {}", bookRequest);
			BookResponse createdBook = bookApiClient.createBook(bookRequest);
			Long bookId = createdBook.bookId();
			log.info("도서 생성 완료: bookId={}", bookId);

			// 5. 이미지 URL을 MinIO에 저장
			if (request.imageUrl() != null && !request.imageUrl().isBlank()) {
				try {
					minioService.uploadImageFromUrl(ImageDomain.BOOK, bookId, request.imageUrl());
					log.info("알라딘 이미지 URL에서 업로드 성공: bookId={}, imageUrl={}", bookId, request.imageUrl());
				} catch (Exception e) {
					log.error("알라딘 이미지 URL 업로드 실패: imageUrl={}", request.imageUrl(), e);
					// 이미지 업로드 실패해도 도서는 생성되었으므로 계속 진행
				}
			}

			return "redirect:/admin/books";
			
		} catch (IllegalArgumentException e) {
			log.error("알라딘 도서 생성 실패 (잘못된 요청): {}", e.getMessage());
			return "redirect:/admin/bookApi/new?error=" + e.getMessage();
		} catch (Exception e) {
			log.error("알라딘 도서 생성 실패", e);
			return "redirect:/admin/bookApi/new?error=도서 생성에 실패했습니다.";
		}
	}
	List<ContributorFindOrCreateRequest> parseContributors(String str, ContributorRole role) {
		List<String> contributorName = Arrays.stream(str.split(",")).toList();
		return contributorName.stream().map(contributor -> new ContributorFindOrCreateRequest(contributor, role)).toList();
	}

	List<Long> findOrCreateContributors(List<ContributorFindOrCreateRequest> contributorList){
		List<Long> contributorIds = new ArrayList<>();
		for (ContributorFindOrCreateRequest contrib : contributorList) {
			try {
				AllContributorResponse contribResponse = contributorApiClient
					.findOrCreateContributor(contrib)
					.getBody();
				if (contribResponse != null) {
					contributorIds.add(contribResponse.contributorId());
					log.info("기여자 추가: {} (ID: {})", contrib.contributorName(), contribResponse.contributorId());
				}
			} catch (Exception e) {
				log.error("기여자 찾기/생성 실패: {} - {}", contrib.contributorName(), e.getMessage());
				// 기여자 실패해도 계속 진행
			}
		}
		return contributorIds;
	}
}
