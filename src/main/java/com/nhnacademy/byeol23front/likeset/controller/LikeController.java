package com.nhnacademy.byeol23front.likeset.controller;

import com.nhnacademy.byeol23front.likeset.client.LikeApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {
    private final LikeApiClient likeApiClient;

    @PostMapping("/likes/{book-id}")
    @ResponseBody
    public void toggleLike(@PathVariable("book-id") Long bookId) {
        likeApiClient.toggleLike(bookId);
    }
}

