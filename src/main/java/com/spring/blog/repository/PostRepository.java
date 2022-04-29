package com.spring.blog.repository;

import java.util.List;

import com.spring.blog.entity.Post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

    // @Query("select p from Post p where p.title like %?1% or p.content like %?1%")
    // Page<Post> findByTitleLikeOrContentLike(String query, Pageable pageable);

    Page<Post> findByTitleOrContentContaining(String title, String content, Pageable pageable);

    Page<Post> findPostsByTagsName(String name, Pageable pageable);
}
