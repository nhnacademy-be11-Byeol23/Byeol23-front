package com.nhnacademy.byeol23front.commons.config;

import com.nhnacademy.byeol23front.commons.interceptor.CategoryHeaderInterceptor;
import com.nhnacademy.byeol23front.commons.interceptor.GuestIdCookieInterceptor;
import com.nhnacademy.byeol23front.resolver.PageableResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new GuestIdCookieInterceptor())
                .addPathPatterns("/**")
                .order(0);

        registry.addInterceptor(new CategoryHeaderInterceptor())
                .addPathPatterns("/")
                .addPathPatterns("/admin/**")
                .addPathPatterns("/members/**")
                .addPathPatterns("/mypage/**")
                .addPathPatterns("/wishlist")
                .addPathPatterns("/carts")
                .addPathPatterns("/search/**")
                .addPathPatterns("/categories/**")
                .addPathPatterns("/books/*")
                .addPathPatterns("/best")
                .addPathPatterns("/new")
                .addPathPatterns("/error/**")
                .order(1);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new PageableResolver());
    }
}
