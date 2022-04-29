package com.spring.blog.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.spring.blog.entity.Comment;
import com.spring.blog.entity.Post;
import com.spring.blog.payload.GetCommentsDto;
import com.spring.blog.repository.CommentRepository;
import com.spring.blog.repository.PostRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class Hello {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    CommentRepository commentRepository;

    @GetMapping
    public Set<Comment> sayHello() {
        List<Post> findAll = postRepository.findAll();
        Set<Comment> comments = findAll.get(0).getComments();
        return comments;
    }

    @GetMapping("/{postId}/comments")
    public List<GetCommentsDto> getPostComments(@PathVariable Long postId) {
        Pageable pageable = PageRequest.of(0, 10);
        List<Comment> commentsList = commentRepository.findByPostId(postId, pageable).getContent();
        return commentsList.stream()
                .map(comment -> GetCommentsDto.builder().id(comment.getId()).content(comment.getBody())
                        .postId(comment.getPost().getId()).build())
                .collect(Collectors.toList());
    }

    @PostMapping
    public String savePost() {
        Post build = Post.builder()
                .title("how to write an article")
                .content("an article written by me")
                .build();
        Post savedPost = postRepository.save(build);
        Comment commentOnPost = Comment.builder().body("new comment").post(savedPost).build();
        Comment save = commentRepository.save(commentOnPost);
        return "saved";

    }

}
