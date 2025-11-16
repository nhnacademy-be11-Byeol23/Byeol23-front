package com.nhnacademy.byeol23front.commons;

import com.nhnacademy.byeol23front.bookset.category.client.CategoryApiClient;
import com.nhnacademy.byeol23front.bookset.category.dto.CategoryTreeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class CommonHeaderModelAttributeAdvice {
    private final CategoryApiClient categoryApiClient;
	// TODO: 차후 삭제
    // @ModelAttribute("tree")
    // public List<CategoryTreeResponse> getRootsWithChildren2Depth() {
    //     return categoryApiClient.getRootsWithChildren2Depth();
    // }
}

