package com.spring.blog.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.blog.payload.PostDto;
import com.spring.blog.service.PostService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(PostController.class)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void givenPostObject_whenCreatePostRequest_thenReturnCreatedPost() throws Exception {
        // given
        PostDto postDtoRequest = PostDto.builder()
                .title("title")
                .content("content")
                .author(null)
                .build();
        PostDto postDtoResponse = PostDto.builder()
                .title("title")
                .content("content")
                .id(1L)
                .author(null)
                .build();
        given(postService.createPost(any(PostDto.class))).willReturn(postDtoResponse);

        // when
        ResultActions requestResult = mockMvc.perform(post("/api/v1/posts")
                .contentType("application/json")
                .content(asJsonString(postDtoRequest)));

        // then
        assertAll(
                () -> verify(this.postService, times(1)).createPost(any(PostDto.class)),
                () -> requestResult.andExpectAll(
                        status().isCreated(),
                        content().contentType("application/json"),
                        jsonPath("$.id").value(1),
                        jsonPath("$.title").value("title"),
                        jsonPath("$.content").value("content")));
    }

    @Test
    void givenPostObject_whenUpdatePostRequest_thenReturnNoContent() throws Exception {
        // given
        PostDto postDtoRequest = PostDto.builder()
                .title("new title")
                .content("new content")
                .author(null)
                .build();

        willDoNothing().given(postService).updatePost(postDtoRequest, 1L);

        // when
        ResultActions requestResult = mockMvc.perform(put("/api/v1/posts/1")
                .contentType("application/json")
                .content(asJsonString(postDtoRequest)));

        // then
        assertAll(
                () -> verify(this.postService, times(1)).updatePost(any(PostDto.class), eq(1L)),
                () -> requestResult.andExpectAll(
                        status().isNoContent(),
                        jsonPath("$").doesNotExist(),
                        content().string("")));
    }

}
