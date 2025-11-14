package com.nhnacademy.byeol23front.config;

import com.nhnacademy.byeol23front.Interceptor.CategoryHeaderInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CategoryHeaderInterceptor())
                .addPathPatterns("/")
                .addPathPatterns("/admin/**")
                .addPathPatterns("/members/login")
                .addPathPatterns("/mypage/**")
                .addPathPatterns("/wishlist")
                .addPathPatterns("/carts");
    }
}
