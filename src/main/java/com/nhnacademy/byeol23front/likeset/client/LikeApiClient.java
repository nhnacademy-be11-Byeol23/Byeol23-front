package com.nhnacademy.byeol23front.likeset.client;

import com.nhnacademy.byeol23front.likeset.dto.LikeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "BYEOL23-GATEWAY", contextId = "likeApiClient")
public interface LikeApiClient {
    @GetMapping("/api/likes")
    List<LikeResponse> getLikes();

    @PostMapping("/api/likes/{book-id}")
    void toggleLike(@PathVariable("book-id") Long bookId);
}
