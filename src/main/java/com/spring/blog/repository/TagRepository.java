package com.spring.blog.repository;

import com.spring.blog.entity.Tag;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

}
