package com.nhnacademy.byeol23front.bookset.book.controller;

import com.nhnacademy.byeol23front.bookset.book.client.BookApiClient;
import com.nhnacademy.byeol23front.bookset.book.dto.*;
import com.nhnacademy.byeol23front.bookset.category.client.CategoryApiClient;
import com.nhnacademy.byeol23front.bookset.category.dto.CategoryLeafResponse;
import com.nhnacademy.byeol23front.bookset.contributor.client.ContributorApiClient;
import com.nhnacademy.byeol23front.bookset.contributor.dto.AllContributorResponse;
import com.nhnacademy.byeol23front.bookset.publisher.client.PublisherApiClient;
import com.nhnacademy.byeol23front.bookset.publisher.dto.AllPublishersInfoResponse;
import com.nhnacademy.byeol23front.bookset.tag.client.TagApiClient;
import com.nhnacademy.byeol23front.bookset.tag.dto.AllTagsInfoResponse;
import com.nhnacademy.byeol23front.bookset.tag.dto.PageResponse;
import com.nhnacademy.byeol23front.minio.dto.back.GetUrlResponse;
import com.nhnacademy.byeol23front.minio.service.MinioService;
import com.nhnacademy.byeol23front.minio.util.ImageDomain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/books")
@RequiredArgsConstructor
public class BookAdminController {
	private final BookApiClient bookApiClient;
	private final CategoryApiClient categoryApiClient;
	private final TagApiClient tagApiClient;
	private final ContributorApiClient contributorApiClient;
	private final PublisherApiClient publisherApiClient;
	private final MinioService minioService;

	private static final String ALL_CONTRIBUTORS = "allContributors";
	private static final String CATEGORIES = "categories";
	private static final String ALL_TAGS = "allTags";
	private static final String ALL_PUBLISHERS = "allPublishers";

	@PostMapping("/new")
	public String createBook(@ModelAttribute BookCreateTmpRequest tmp) {
		try {
			List<Long> categoryIds = tmp.categoryIds() != null ? tmp.categoryIds() : List.of();
			List<Long> tagIds = tmp.tagIds() != null ? tmp.tagIds() : List.of();
			List<Long> contributorIds = tmp.contributorIds() != null ? tmp.contributorIds() : List.of();
			
			BookCreateRequest request = new BookCreateRequest(
				tmp.bookName(),
				tmp.toc(),
				tmp.description(),
				tmp.regularPrice(),
				tmp.salePrice(),
				tmp.isbn(),
				tmp.publishDate(),
				tmp.isPack(),
				tmp.bookStatus(),
				tmp.stock(),
				tmp.publisherId(),
				categoryIds,
				tagIds,
				contributorIds
			);

			BookResponse createdBook = bookApiClient.createBook(request);
			Long bookId = createdBook.bookId();
			log.info("도서 생성 완료: bookId={}", bookId);

			if (tmp.images() != null && !tmp.images().isEmpty()) {
				for (MultipartFile image : tmp.images()) {
					if (!image.isEmpty()) {
						try {
							minioService.uploadImage(ImageDomain.BOOK, bookId, image);
							log.info("이미지 업로드 성공: bookId={}, fileName={}", bookId, image.getOriginalFilename());
						} catch (Exception e) {
							log.error("이미지 업로드 실패: fileName={}", image.getOriginalFilename(), e);
						}
					}
				}
			}
			return "redirect:/admin/books";
			
		} catch (Exception e) {
			log.error("도서 생성 실패", e);
			return "redirect:/admin/books/new";
		}
	}

	@PostMapping("/{book-id}")
	public String updateBook(@PathVariable("book-id") Long bookId, @ModelAttribute BookUpdateTmpRequest tmp) {
		try {
			List<Long> categoryIds = tmp.categoryIds() != null ? tmp.categoryIds() : List.of();
			List<Long> tagIds = tmp.tagIds() != null ? tmp.tagIds() : List.of();
			List<Long> contributorIds = tmp.contributorIds() != null ? tmp.contributorIds() : List.of();
			
			if (tmp.images() != null && !tmp.images().isEmpty()) {
				boolean hasNewImages = tmp.images().stream().anyMatch(img -> !img.isEmpty());
				if (hasNewImages) {
					try {
						List<GetUrlResponse> existingImages = minioService.getImageUrl(ImageDomain.BOOK, bookId);
						for (GetUrlResponse imageResponse : existingImages) {
							try {
								minioService.deleteImage(ImageDomain.BOOK, imageResponse.imageId());
								log.info("기존 이미지 삭제: imageId={}", imageResponse.imageId());
							} catch (Exception e) {
								log.error("이미지 삭제 실패: imageId={}", imageResponse.imageId(), e);
							}
						}
					} catch (Exception e) {
						log.warn("기존 이미지 조회 실패 (이미지가 없을 수 있음): {}", e.getMessage());
					}
					for (MultipartFile image : tmp.images()) {
						if (!image.isEmpty()) {
							try {
								minioService.uploadImage(ImageDomain.BOOK, bookId, image);
								log.info("새 이미지 업로드 성공: bookId={}, fileName={}", bookId, image.getOriginalFilename());
							} catch (Exception e) {
								log.error("이미지 업로드 실패: fileName={}", image.getOriginalFilename(), e);
							}
						}
					}
				}
			}
			
			BookUpdateRequest request = new BookUpdateRequest(
				tmp.bookName(),
				tmp.toc(),
				tmp.description(),
				tmp.regularPrice(),
				tmp.salePrice(),
				tmp.publishDate(),
				tmp.isPack() != null && tmp.isPack(),
				tmp.bookStatus(),
				tmp.publisherId(),
				categoryIds,
				tagIds,
				contributorIds
			);
			bookApiClient.updateBook(bookId, request);
			return "redirect:/admin/books";
			
		} catch (Exception e) {
			log.error("도서 수정 실패: bookId={}", bookId, e);
			return "redirect:/admin/books/update/" + bookId;
		}
	}

	@GetMapping("/update/{book-id}")
	public String bookUpdateForm(@PathVariable("book-id") Long id, Model model){
		BookResponse book = bookApiClient.getBook(id).getBody();
		model.addAttribute("book", book);
		model.addAttribute(CATEGORIES, categoryApiClient.getRoots());

		PageResponse<AllTagsInfoResponse> tagsResponse = tagApiClient.getAllTags(0, 100).getBody();
		List<AllTagsInfoResponse> allTags = tagsResponse != null ? tagsResponse.content() : new ArrayList<>();
		model.addAttribute(ALL_TAGS, allTags);

		com.nhnacademy.byeol23front.bookset.contributor.dto.PageResponse<AllContributorResponse> contributorsResponse = contributorApiClient.getAllContributors(0, 1000).getBody();
		List<AllContributorResponse> allContributors = contributorsResponse != null ? contributorsResponse.content() : new ArrayList<>();
		model.addAttribute(ALL_CONTRIBUTORS, allContributors);

		com.nhnacademy.byeol23front.bookset.publisher.dto.PageResponse<AllPublishersInfoResponse> publishersResponse = publisherApiClient.getAllPublishers(0, 100).getBody();
		List<AllPublishersInfoResponse> allPublishers = publishersResponse != null ? publishersResponse.content() : new ArrayList<>();
		model.addAttribute(ALL_PUBLISHERS, allPublishers);

		List<Long> selectedCategoryIds = book.categories() != null
			? book.categories().stream().map(CategoryLeafResponse::id).toList()
			: Collections.emptyList();
		model.addAttribute("selectedCategoryIds", selectedCategoryIds);

		List<Long> selectedTagIds = book.tags() != null
			? book.tags().stream().map(AllTagsInfoResponse::tagId).toList()
			: Collections.emptyList();
		model.addAttribute("selectedTagIds", selectedTagIds);

		List<Long> selectedContributorIds = book.contributors() != null
			? book.contributors().stream().map(AllContributorResponse::contributorId).toList()
			: Collections.emptyList();
		model.addAttribute("selectedContributorIds", selectedContributorIds);

		return "admin/book/bookUpdateForm";
	}

	@GetMapping("/update/{book-id}/stock")
	public String getBookStock(Model model, @PathVariable("book-id")Long bookId){
		BookStockResponse bookStock = bookApiClient.getBookStock(bookId);
		model.addAttribute("book",bookStock);
		return "admin/book/bookStockUpdateForm";
	}

	@PostMapping("/update/{book-id}/stock")
	public String updateBookStock(@PathVariable("book-id")Long bookId, BookStockUpdateRequest request){
		bookApiClient.updateBookStock(bookId, request);
		return "redirect:/admin/books";
	}


	@GetMapping
	public String getBooks(Model model){
		model.addAttribute("books", bookApiClient.getBooks(0,20));
		return "admin/book/bookList";
	}

	@GetMapping("/new")
	public String bookForm(
		@RequestParam(required = false) String fromAladin,
		Model model
	) {
		model.addAttribute(CATEGORIES, categoryApiClient.getRoots());

		PageResponse<AllTagsInfoResponse> tagsResponse = tagApiClient.getAllTags(0, 100).getBody();
		List<AllTagsInfoResponse> allTags = tagsResponse != null ? tagsResponse.content() : new ArrayList<>();
		model.addAttribute(ALL_TAGS, allTags);

		com.nhnacademy.byeol23front.bookset.contributor.dto.PageResponse<AllContributorResponse> contributorsResponse = contributorApiClient.getAllContributors(0, 1000).getBody();
		List<AllContributorResponse> allContributors = contributorsResponse != null ? contributorsResponse.content() : new ArrayList<>();
		model.addAttribute(ALL_CONTRIBUTORS, allContributors);

		com.nhnacademy.byeol23front.bookset.publisher.dto.PageResponse<AllPublishersInfoResponse> publishersResponse = publisherApiClient.getAllPublishers(0, 100).getBody();
		List<AllPublishersInfoResponse> allPublishers = publishersResponse != null ? publishersResponse.content() : new ArrayList<>();
		model.addAttribute(ALL_PUBLISHERS, allPublishers);

		if ("true".equals(fromAladin)) {
			model.addAttribute("fromAladin", true);
		}
		
		return "admin/book/bookForm";
	}

	@DeleteMapping("/{book-id}")
	@ResponseBody
	public ResponseEntity<Void> deleteBook(@PathVariable("book-id") Long id) {
		try {
			bookApiClient.deleteBook(id);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}
}
