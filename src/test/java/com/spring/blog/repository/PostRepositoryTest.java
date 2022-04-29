package com.spring.blog.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.spring.blog.entity.Post;
import com.spring.blog.entity.Tag;
import com.spring.blog.entity.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
public class PostRepositoryTest {

        @Autowired
        private PostRepository postRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private TagRepository tagRepository;

        private Post post;

        @BeforeEach
        public void setup() {
                post = Post.builder()
                                .title("how to write an article")
                                .content("an article written by me")
                                .build();
        }

        @Test
        public void givenPostObject_whenSave_thenReturnSavedPost() {

                // when
                Post savedPost = postRepository.save(post);

                // then
                assertAll(
                                () -> assertNotNull(savedPost),
                                () -> assertTrue(savedPost.getId() > 0),
                                () -> assertEquals(post.getTitle(), savedPost.getTitle()),
                                () -> assertEquals(post.getContent(), savedPost.getContent()));
        }

        @Test
        public void givenPostList_whenFindAll_thenPostListReturned() {

                // given
                Post post1 = Post.builder()
                                .title("foo bar")
                                .content("article written by foo bar")
                                .build();
                postRepository.save(post);
                postRepository.save(post1);

                // when
                List<Post> allPosts = postRepository.findAll();
                // then
                assertAll(
                                () -> assertNotNull(allPosts),
                                () -> assertEquals(2, allPosts.size()));
        }

        @Test
        public void givenPostList_whenFindByTitleOrContentContaining_thenPaginatedPostListOfLikeTitleOrContentReturned() {

                // given
                Post post1 = Post.builder()
                                .title("foo bar")
                                .content("article written by foo bar")
                                .build();
                Post post2 = Post.builder()
                                .title("fizz buzz")
                                .content("article written by foobar")
                                .build();
                Post post3 = Post.builder()
                                .title("foo")
                                .content("article written by foo")
                                .build();
                postRepository.save(post);
                postRepository.save(post1);
                postRepository.save(post2);
                postRepository.save(post3);

                // when
                Pageable pageable = PageRequest.of(0, 10);
                Page<Post> pagedPosts = postRepository.findByTitleOrContentContaining("bar", "bar", pageable);
                List<Post> allPosts = pagedPosts.getContent();

                // then
                assertAll(
                                () -> assertNotNull(allPosts),
                                () -> assertEquals(2, allPosts.size()),
                                () -> assertEquals(2, pagedPosts.getTotalElements()),
                                () -> assertEquals(1, pagedPosts.getTotalPages()),
                                () -> assertEquals(0, pagedPosts.getNumber()),
                                () -> assertTrue(pagedPosts.isLast()));
        }

        @Test
        public void givenPost_whenDelete_thenPostDeleted() {

                // given
                postRepository.save(post);

                // when
                postRepository.deleteById(post.getId());
                Optional<Post> deletedPost = postRepository.findById(post.getId());

                // then
                assertTrue(deletedPost.isEmpty());
        }

        @Test
        public void givenTagName_whenFindPostsByTagName_thenAllPostsWithTagReturned() {

                // given
                String tagName = "tag1";
                Post post1 = Post.builder()
                                .title("post1")
                                .content("article written by foo bar")
                                .build();
                Post post2 = Post.builder()
                                .title("post2")
                                .content("article written by foobar")
                                .build();
                Post post3 = Post.builder()
                                .title("foo")
                                .content("article written by foo")
                                .build();
                postRepository.save(post);
                postRepository.save(post1);
                postRepository.save(post2);
                postRepository.save(post3);

                Set<Post> posts = new HashSet<>();
                posts.add(post1);
                posts.add(post2);
                tagRepository.save(Tag.builder().name(tagName).posts(posts).build());
                tagRepository.save(Tag.builder().name("tag2").build());

                // when
                Pageable pageable = PageRequest.of(0, 10);
                Page<Post> pagedPosts = postRepository.findPostsByTagsName(tagName, pageable);
                List<Post> allPosts = pagedPosts.getContent();

                // then
                assertAll(
                                () -> assertNotNull(allPosts),
                                () -> assertEquals(2, allPosts.size()),
                                () -> assertEquals(2, pagedPosts.getTotalElements()),
                                () -> assertEquals(1, pagedPosts.getTotalPages()),
                                () -> assertEquals(0, pagedPosts.getNumber()),
                                () -> assertTrue(pagedPosts.isLast()));
        }
}
