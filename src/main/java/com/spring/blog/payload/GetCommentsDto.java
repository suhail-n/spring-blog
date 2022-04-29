package com.spring.blog.payload;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetCommentsDto {
    private long id;
    private String content;
    private long postId;
}
