package com.spring.blog.payload;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class UserDto {

    private long id;

    @NotEmpty
    private String username;

    @NotEmpty
    private String email;

    @NotEmpty
    private String password;

    private String firstName;
    private String lastName;

}
