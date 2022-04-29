package com.spring.blog.controller;

import javax.websocket.server.PathParam;

import com.spring.blog.payload.PostDto;
import com.spring.blog.service.PostService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostDto> createPost(PostDto postDto) {
        return new ResponseEntity<>(postService.createPost(postDto), HttpStatus.CREATED);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<String> updatePost(PostDto postDto, @PathVariable Long postId) {
        postService.updatePost(postDto, postId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
