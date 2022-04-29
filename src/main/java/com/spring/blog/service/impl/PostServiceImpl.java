package com.spring.blog.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.spring.blog.entity.Post;
import com.spring.blog.exceptions.ResourceNotFoundException;
import com.spring.blog.payload.PaginatedResponseDto;
import com.spring.blog.payload.PostDto;
import com.spring.blog.repository.PostRepository;
import com.spring.blog.service.PostService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PostDto createPost(PostDto postDto) {
        Post postEntity = this.modelMapper.map(postDto, Post.class);
        Post savedEntity = this.postRepository.save(postEntity);
        return this.modelMapper.map(savedEntity, PostDto.class);
    }

    @Override
    public void deletePost(Long postId) {
        Post foundPost = this.postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        this.postRepository.delete(foundPost);
    }

    @Override
    public void updatePost(PostDto postDto, Long postId) {
        Post foundPost = this.postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        foundPost.setContent(postDto.getContent());
        foundPost.setTitle(postDto.getTitle());
        this.postRepository.save(foundPost);
    }

    @Override
    public PaginatedResponseDto<PostDto> getAllPosts(int pageNo, int pageSize, String query, String tag) {
        Pageable pageable = PageRequest.of(pageSize, pageNo);
        Page<Post> pagedAllPosts;
        if (tag != null) {
            pagedAllPosts = this.postRepository.findPostsByTagsName(tag, pageable);
        } else if (query != null) {
            pagedAllPosts = this.postRepository.findByTitleOrContentContaining(query, query, pageable);
        } else {
            pagedAllPosts = this.postRepository.findAll(pageable);
        }

        List<Post> allPosts = pagedAllPosts.getContent();
        List<PostDto> posts = allPosts.stream().map(post -> this.modelMapper.map(post, PostDto.class))
                .collect(Collectors.toList());

        return PaginatedResponseDto.<PostDto>builder()
                .content(posts)
                .pageNo(pagedAllPosts.getNumber())
                .pageSize(pagedAllPosts.getSize())
                .totalElements(pagedAllPosts.getTotalElements())
                .totalPages(pagedAllPosts.getTotalPages())
                .last(pagedAllPosts.isLast()).build();
    }

}
