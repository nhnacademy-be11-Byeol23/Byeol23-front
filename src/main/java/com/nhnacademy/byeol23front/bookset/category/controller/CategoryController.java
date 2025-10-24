package com.nhnacademy.byeol23front.bookset.category.controller;

import com.nhnacademy.byeol23front.bookset.category.client.CategoryApiClient;
import com.nhnacademy.byeol23front.bookset.category.dto.CategoryCreateRequest;
import com.nhnacademy.byeol23front.bookset.category.dto.CategoryListResponse;
import com.nhnacademy.byeol23front.bookset.category.dto.CategoryUpdateRequest;
import com.nhnacademy.byeol23front.bookset.category.dto.CategoryUpdateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryApiClient categoryApiClient;

    @PostMapping("/categories")
    public void createCategory(@RequestBody CategoryCreateRequest createRequest) {
        categoryApiClient.createCategory(createRequest);
    }

    @PutMapping("/categories/{id}")
    public CategoryUpdateResponse updateCategory(@PathVariable("id") Long id, @RequestBody CategoryUpdateRequest updateRequest) {
        return categoryApiClient.updateCategory(id, updateRequest);
    }

    @DeleteMapping("/categories/{id}")
    public void deleteCategory(@PathVariable("id") Long id) {
        categoryApiClient.deleteCategory(id);
    }

    @GetMapping("/categories/{parentId}/children")
    public List<CategoryListResponse> getChildren(@PathVariable("parentId") Long parentId) {
        return categoryApiClient.getChildren(parentId);
    }
}
