package com.spring.blog.payload;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class CommentDto {

    private long id;

    @NotEmpty
    private String body;

    private UserDto author;
}
