package com.spring.blog.payload;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

    private long id;

    @NotEmpty
    @Size(min = 3, message = "Post title should have at least 3 characters")
    private String title;

    @NotEmpty
    private String content;

    private UserDto author;
}
