package com.nhnacademy.byeol23front.commons;

import com.nhnacademy.byeol23front.bookset.category.client.CategoryApiClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class CategoryHeaderAdvice {
    private final CategoryApiClient categoryApiClient;

    @ModelAttribute("tree")
    public void getRootsWithChildren2Depth(HttpServletRequest request, Model model) {
        Boolean isRequiredHeader = (Boolean) request.getAttribute("requiredHeader");
        if(Boolean.TRUE.equals(isRequiredHeader)) {
            model.addAttribute("tree", categoryApiClient.getRootsWithChildren2Depth());
        }
    }
}

