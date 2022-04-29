package com.spring.blog.repository;

import com.spring.blog.entity.Comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPostId(long postId, Pageable pageable);
}
