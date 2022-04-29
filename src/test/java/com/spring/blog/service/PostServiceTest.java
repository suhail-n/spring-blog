package com.spring.blog.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Optional;

import com.spring.blog.entity.Post;
import com.spring.blog.exceptions.ResourceNotFoundException;
import com.spring.blog.payload.PaginatedResponseDto;
import com.spring.blog.payload.PostDto;
import com.spring.blog.repository.PostRepository;
import com.spring.blog.service.impl.PostServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @InjectMocks
    private PostServiceImpl postService;

    @Mock
    private PostRepository postRepository;

    // @Autowired
    // private ModelMapper modelMapper;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    // convert DTO to entity
    Post mapToEntity(PostDto postDto) {

        Post post = new ModelMapper().map(postDto, Post.class);
        return post;
    }

    @Test
    void givenPostObject_whenCreatePost_thenReturnSavedPost() {

        // given
        PostDto postDto = PostDto.builder()
                .title("how to write an article")
                .content("an article written by me")
                .build();
        Post postEntity = mapToEntity(postDto);
        postEntity.setId(1);

        given(this.postRepository.save(any())).willReturn(postEntity);

        given(this.modelMapper.map(postDto, Post.class)).willReturn(postEntity);
        postDto.setId(1);
        given(this.modelMapper.map(postEntity, PostDto.class)).willReturn(postDto);

        // when
        PostDto savedPost = postService.createPost(postDto);

        // then
        assertAll(
                () -> verify(this.modelMapper, times(1)).map(postDto, Post.class),
                () -> verify(this.modelMapper, times(1)).map(postEntity, PostDto.class),
                () -> assertNotNull(savedPost),
                () -> assertEquals(1, savedPost.getId()),
                () -> assertEquals(postDto.getTitle(), savedPost.getTitle()),
                () -> assertEquals(postDto.getContent(), savedPost.getContent()));
    }

    @Test
    void givenPostIdOfExistingPost_whenDeletePost_thenNothing() {

        // given
        Long postId = 1L;
        Post existingPost = Post.builder().id(postId).content("content").title("title").build();
        given(this.postRepository.findById(postId)).willReturn(Optional.of(existingPost));
        willDoNothing().given(postRepository).delete(existingPost);

        // when
        postService.deletePost(postId);

        // then
        verify(this.postRepository, times(1)).findById(postId);
        verify(this.postRepository, times(1)).delete(existingPost);
    }

    @Test
    void givenPostIdOfNonExistingPost_whenDeletePost_thenThrowResourceNotFoundException() {

        // given
        Long postId = 1L;
        given(this.postRepository.findById(postId)).willReturn(Optional.empty());

        // when
        Executable executable = () -> this.postService.deletePost(postId);

        // then
        ResourceNotFoundException thrownException = assertThrows(ResourceNotFoundException.class, executable);
        assertEquals("Post not found with id : '1'", thrownException.getMessage());
    }

    @Test
    void givenPostObject_whenUpdatePost_thenPostUpdated() {
        // given
        Long postId = 1L;
        PostDto postDto = PostDto.builder()
                .title("how to write an article")
                .content("an article written by me")
                .build();
        Post existingPost = Post.builder().id(postId).content("content").title("title").build();
        given(this.postRepository.findById(postId)).willReturn(Optional.of(existingPost));
        given(this.postRepository.save(existingPost)).willReturn(existingPost);

        // when
        this.postService.updatePost(postDto, 1L);

        // then
        assertAll(
                () -> verify(this.postRepository, times(1)).findById(postId),
                () -> verify(this.postRepository, times(1)).save(existingPost),
                () -> assertEquals("how to write an article", existingPost.getTitle()),
                () -> assertEquals("an article written by me", existingPost.getContent()));
    }

    @Test
    void givenPostIdOfNonExistingPost_whenUpdatePost_thenThrowResourceNotFoundException() {

        // given
        Long postId = 1L;
        PostDto postDto = PostDto.builder()
                .title("how to write an article")
                .content("an article written by me")
                .build();
        given(this.postRepository.findById(postId)).willReturn(Optional.empty());

        // when
        Executable executable = () -> this.postService.updatePost(postDto, postId);

        // then
        ResourceNotFoundException thrownException = assertThrows(ResourceNotFoundException.class, executable);
        assertEquals("Post not found with id : '1'", thrownException.getMessage());
    }

    @Test
    void givenTagName_whenGetAllPosts_thenReturnPaginatedPostsWithTag() {

        // given
        String tagName = "tag";
        Post post = Post.builder().id(1L).content("content").title("title").build();
        Page<Post> paginatedPost = mock(Page.class);

        given(paginatedPost.getContent()).willReturn(Arrays.asList(post));
        given(paginatedPost.getTotalPages()).willReturn(1);
        given(paginatedPost.getTotalElements()).willReturn(1L);
        given(paginatedPost.getNumber()).willReturn(0);
        given(paginatedPost.getSize()).willReturn(1);
        given(this.postRepository.findPostsByTagsName(eq(tagName), any())).willReturn(paginatedPost);

        // when
        PaginatedResponseDto<PostDto> allPosts = this.postService.getAllPosts(1, 10, null, tagName);

        // then
        assertAll(
                () -> verify(this.postRepository, times(1)).findPostsByTagsName(eq(tagName), any()),
                () -> assertNotNull(allPosts),
                () -> assertEquals(1, allPosts.getTotalPages()),
                () -> assertEquals(1L, allPosts.getTotalElements()),
                () -> assertEquals(0, allPosts.getPageNo()),
                () -> assertEquals(1, allPosts.getPageSize()),
                () -> assertEquals(1, allPosts.getContent().size()),
                () -> assertEquals(post.getId(), allPosts.getContent().get(0).getId()),
                () -> assertEquals(post.getTitle(), allPosts.getContent().get(0).getTitle()),
                () -> assertEquals(post.getContent(), allPosts.getContent().get(0).getContent()));
    }

    @Test
    void givenQuery_whenGetAllPosts_thenReturnPaginatedPostsWithTitleOrContentContainingQuery() {

        // given
        String query = "content";
        Post post = Post.builder().id(1L).content("content").title("title").build();
        Page<Post> paginatedPost = mock(Page.class);

        given(paginatedPost.getContent()).willReturn(Arrays.asList(post));
        given(paginatedPost.getTotalPages()).willReturn(1);
        given(paginatedPost.getTotalElements()).willReturn(1L);
        given(paginatedPost.getNumber()).willReturn(0);
        given(paginatedPost.getSize()).willReturn(1);
        given(this.postRepository.findByTitleOrContentContaining(any(), any(), any())).willReturn(paginatedPost);

        // when
        PaginatedResponseDto<PostDto> allPosts = this.postService.getAllPosts(1, 10, query, null);

        // then
        assertAll(
                () -> verify(this.postRepository, times(1)).findByTitleOrContentContaining(eq(query), eq(query), any()),
                () -> assertNotNull(allPosts),
                () -> assertEquals(1, allPosts.getTotalPages()),
                () -> assertEquals(1L, allPosts.getTotalElements()),
                () -> assertEquals(0, allPosts.getPageNo()),
                () -> assertEquals(1, allPosts.getPageSize()),
                () -> assertEquals(1, allPosts.getContent().size()),
                () -> assertEquals(post.getId(), allPosts.getContent().get(0).getId()),
                () -> assertEquals(post.getTitle(), allPosts.getContent().get(0).getTitle()),
                () -> assertEquals(post.getContent(), allPosts.getContent().get(0).getContent()));
    }

    @Test
    void givenNoQueriesOrTags_whenGetAllPosts_thenReturnAllPostsPaginated() {

        // given
        Post post = Post.builder().id(1L).content("content").title("title").build();
        Page<Post> paginatedPost = mock(Page.class);

        given(paginatedPost.getContent()).willReturn(Arrays.asList(post));
        given(paginatedPost.getTotalPages()).willReturn(1);
        given(paginatedPost.getTotalElements()).willReturn(1L);
        given(paginatedPost.getNumber()).willReturn(0);
        given(paginatedPost.getSize()).willReturn(1);
        given(this.postRepository.findAll(any(Pageable.class))).willReturn(paginatedPost);

        // when
        PaginatedResponseDto<PostDto> allPosts = this.postService.getAllPosts(1, 10, null, null);

        // then
        assertAll(
                () -> verify(this.postRepository, times(1)).findAll(any(Pageable.class)),
                () -> assertNotNull(allPosts),
                () -> assertEquals(1, allPosts.getTotalPages()),
                () -> assertEquals(1L, allPosts.getTotalElements()),
                () -> assertEquals(0, allPosts.getPageNo()),
                () -> assertEquals(1, allPosts.getPageSize()),
                () -> assertEquals(1, allPosts.getContent().size()),
                () -> assertEquals(post.getId(), allPosts.getContent().get(0).getId()),
                () -> assertEquals(post.getTitle(), allPosts.getContent().get(0).getTitle()),
                () -> assertEquals(post.getContent(), allPosts.getContent().get(0).getContent()));
    }
}
