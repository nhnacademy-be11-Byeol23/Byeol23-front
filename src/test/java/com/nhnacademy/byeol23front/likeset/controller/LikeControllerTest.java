package com.nhnacademy.byeol23front.likeset.controller;

import com.nhnacademy.byeol23front.bookset.category.client.CategoryApiClient;
import com.nhnacademy.byeol23front.likeset.client.LikeApiClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LikeController.class)
class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LikeApiClient likeApiClient;

    // CategoryHeaderAdvice 때문에 필요
    @MockBean
    private CategoryApiClient categoryApiClient;

    @Test
    @DisplayName("POST /likes/{book-id} 호출 시 LikeApiClient.toggleLike 호출")
    void toggleLike_callsApiClient() throws Exception {
        // given
        Long bookId = 1L;

        // when
        mockMvc.perform(post("/likes/{book-id}", bookId)
                        .with(csrf())
                        .with(user("user").roles("USER")))
                .andExpect(status().isOk());

        // then
        then(likeApiClient).should().toggleLike(Mockito.eq(bookId));
    }
}


