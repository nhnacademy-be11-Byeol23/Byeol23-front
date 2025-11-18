package com.nhnacademy.byeol23front.resolver;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PageableResolver implements HandlerMethodArgumentResolver {
    private static final int MAX_PAGE_SIZE = 20;
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(Pageable.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        int page = Integer.parseInt(StringUtils.defaultIfBlank(request.getParameter("page"), "0"));
        int size = Math.min(Integer.parseInt(StringUtils.defaultIfBlank(request.getParameter("size"), "10")), MAX_PAGE_SIZE);
        return PageRequest.of(page, size);
    }
}