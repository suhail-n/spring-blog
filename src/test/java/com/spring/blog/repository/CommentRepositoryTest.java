package com.spring.blog.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.spring.blog.entity.Comment;
import com.spring.blog.entity.Post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    public void givenPostId_whenFindCommentsByPost_thenReturnComments() {

        // given
        Post post = Post.builder()
                .title("how to write an article")
                .content("an article written by me")
                .build();
        postRepository.save(post);

        commentRepository.save(Comment.builder()
                .post(post)
                .body("this is a comment")
                .build());

        commentRepository.save(Comment.builder()
                .post(post)
                .body("this is a 2nd comment")
                .build());

        // when
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> pagedComments = commentRepository.findByPostId(post.getId(), pageable);
        List<Comment> comments = pagedComments.getContent();

        // then
        assertAll(
                () -> assertNotNull(comments),
                () -> assertEquals(2, comments.size()),
                () -> assertEquals(2, pagedComments.getTotalElements()),
                () -> assertEquals(1, pagedComments.getTotalPages()),
                () -> assertEquals(0, pagedComments.getNumber()),
                () -> assertTrue(pagedComments.isLast()));
    }
}
