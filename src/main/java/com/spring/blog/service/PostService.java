package com.spring.blog.service;

import com.spring.blog.payload.PaginatedResponseDto;
import com.spring.blog.payload.PostDto;

public interface PostService {
    PostDto createPost(PostDto postDto);

    void deletePost(Long postId);

    void updatePost(PostDto postDto, Long postId);

    PaginatedResponseDto<PostDto> getAllPosts(int pageNo, int pageSize, String query, String tag);
}
