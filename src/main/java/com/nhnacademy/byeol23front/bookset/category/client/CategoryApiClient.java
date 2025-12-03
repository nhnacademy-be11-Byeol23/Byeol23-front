package com.nhnacademy.byeol23front.bookset.category.client;

import com.nhnacademy.byeol23front.bookset.category.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "BYEOL23-GATEWAY", contextId = "categoryApiClient")
public interface CategoryApiClient {

    @PostMapping("/api/categories")
    void createCategory(CategoryCreateRequest createRequest);

    @PutMapping("/api/categories/{id}")
    CategoryUpdateResponse updateCategory(@PathVariable("id") Long id, @RequestBody CategoryUpdateRequest updateRequest);

    @DeleteMapping("/api/categories/{id}")
    void deleteCategory(@PathVariable("id") Long id);

    @GetMapping("/api/categories/roots")
    List<CategoryListResponse> getRoots();

    @GetMapping("/api/categories/leaf")
    List<CategoryLeafResponse> getLeafs();

	@GetMapping("/api/categories/leaf/mainpage")
	List<CategoryMainPageResponse> getLeavesForMainPage();

    @GetMapping("/api/categories/{parentId}/children")
    List<CategoryListResponse> getChildren(@PathVariable("parentId") Long parentId);

    @GetMapping("/api/categories/tree")
    List<CategoryTreeResponse> getRootsWithChildren2Depth();
}
