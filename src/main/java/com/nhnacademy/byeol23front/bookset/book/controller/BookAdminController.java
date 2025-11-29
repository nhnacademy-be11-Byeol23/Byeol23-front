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
import feign.FeignException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

	@Operation(summary = "도서 생성", description = "새로운 도서를 생성하고 이미지를 업로드합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "302", description = "도서 생성 성공 (리다이렉트)"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 또는 카테고리 미선택")
	})
	@PostMapping("/new")
	public String createBook(@ModelAttribute BookCreateTmpRequest tmp, RedirectAttributes redirectAttributes) {
		List<Long> categoryIds = tmp.categoryIds() != null ? tmp.categoryIds() : List.of();
		List<Long> tagIds = tmp.tagIds() != null ? tmp.tagIds() : List.of();
		List<Long> contributorIds = tmp.contributorIds() != null ? tmp.contributorIds() : List.of();

		if (categoryIds.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "카테고리를 최소 한 개 이상 선택해야 합니다.");
			return "redirect:/admin/books/new";
		}

		try {
			BookCreateRequest request = new BookCreateRequest(
				tmp.bookName(),
				tmp.toc(),
				tmp.description(),
				tmp.regularPrice(),
				tmp.salePrice(),
				tmp.isbn(),
				tmp.publishDate(),
				Boolean.TRUE.equals(tmp.isPack()),
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

		} catch (FeignException.BadRequest e) {
			redirectAttributes.addFlashAttribute("errorMessage", "카테고리를 최소 한 개 이상 선택해야 합니다.");
			return "redirect:/admin/books/new";
		} catch (Exception e) {
			log.error("도서 생성 실패", e);
			redirectAttributes.addFlashAttribute("errorMessage", "도서 생성 중 오류가 발생했습니다.");
			return "redirect:/admin/books/new";
		}
	}

	@Operation(summary = "도서 수정", description = "도서 정보를 수정하고 이미지를 업데이트합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "302", description = "도서 수정 성공 (리다이렉트)"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 또는 카테고리 미선택")
	})
	@Parameter(name = "book-id", description = "도서 ID", required = true, example = "1")
	@PostMapping("/{book-id}")
	public String updateBook(@PathVariable("book-id") Long bookId, @ModelAttribute BookUpdateTmpRequest tmp,
		RedirectAttributes redirectAttributes) {
		List<Long> categoryIds = tmp.categoryIds() != null ? tmp.categoryIds() : List.of();
		List<Long> tagIds = tmp.tagIds() != null ? tmp.tagIds() : List.of();
		List<Long> contributorIds = tmp.contributorIds() != null ? tmp.contributorIds() : List.of();

		if (categoryIds.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "카테고리를 최소 한 개 이상 선택해야 합니다.");
			return "redirect:/admin/books/update/" + bookId;
		}

		try {
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
			
		} catch (FeignException.BadRequest e) {
			redirectAttributes.addFlashAttribute("errorMessage", "카테고리를 최소 한 개 이상 선택해야 합니다.");
			return "redirect:/admin/books/update/" + bookId;
		} catch (Exception e) {
			log.error("도서 수정 실패: bookId={}", bookId, e);
			redirectAttributes.addFlashAttribute("errorMessage", "도서 수정 중 오류가 발생했습니다.");
			return "redirect:/admin/books/update/" + bookId;
		}
	}

	@Operation(summary = "도서 수정 폼", description = "도서 수정을 위한 폼 페이지를 표시합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "도서 수정 폼 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
	})
	@Parameter(name = "book-id", description = "도서 ID", required = true, example = "1")
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

	@Operation(summary = "도서 재고 수정 폼", description = "도서 재고 수정을 위한 폼 페이지를 표시합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "도서 재고 수정 폼 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
	})
	@Parameter(name = "book-id", description = "도서 ID", required = true, example = "1")
	@GetMapping("/update/{book-id}/stock")
	public String getBookStock(Model model, @PathVariable("book-id")Long bookId){
		BookStockResponse bookStock = bookApiClient.getBookStock(bookId);
		model.addAttribute("book",bookStock);
		return "admin/book/bookStockUpdateForm";
	}

	@Operation(summary = "도서 재고 수정", description = "도서의 재고를 수정합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "302", description = "도서 재고 수정 성공 (리다이렉트)"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
	})
	@Parameter(name = "book-id", description = "도서 ID", required = true, example = "1")
	@PostMapping("/update/{book-id}/stock")
	public String updateBookStock(@PathVariable("book-id")Long bookId, BookStockUpdateRequest request){
		bookApiClient.updateBookStock(bookId, request);
		return "redirect:/admin/books";
	}


	@Operation(summary = "도서 목록 조회", description = "페이징된 도서 목록을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "도서 목록 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
	})
	@Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0")
	@Parameter(name = "size", description = "페이지 크기", example = "20")
	@GetMapping
	public String getBooks(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "20") int size,
		Model model
	){
		PageResponse<BookResponse> response = bookApiClient.getBooks(page, size).getBody();
		if (response == null) {
			response = new PageResponse<>(List.of(), page, size, 0, 0, true, true);
		}

		model.addAttribute("books", response.content());
		model.addAttribute("paging", response);
		return "admin/book/bookList";
	}

	@Operation(summary = "도서 생성 폼", description = "새로운 도서를 생성하기 위한 폼 페이지를 표시합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "도서 생성 폼 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
	})
	@Parameter(name = "fromAladin", description = "알라딘 API에서 온 요청 여부", required = false, example = "true")
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

	@Operation(summary = "도서 삭제", description = "도서를 삭제합니다 (Soft Delete).")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "도서 삭제 성공"),
		@ApiResponse(responseCode = "400", description = "삭제 실패 또는 잘못된 요청")
	})
	@Parameter(name = "book-id", description = "도서 ID", required = true, example = "1")
	@PostMapping("/{book-id}/delete")
	@ResponseBody
	public ResponseEntity<Void> deleteBook(@PathVariable("book-id") Long id) {
		log.info("[BookAdminController] DELETE 요청 수신: bookId={}", id);
		try {
			bookApiClient.deleteBook(id);
			log.info("[BookAdminController] 도서 삭제 성공: bookId={}", id);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			log.error("[BookAdminController] 도서 삭제 실패: bookId={}, error={}", id, e.getMessage(), e);
			return ResponseEntity.internalServerError().build();
		}
	}
}