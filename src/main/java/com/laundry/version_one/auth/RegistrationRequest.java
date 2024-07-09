package com.laundry.version_one.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationRequest {
    @NotEmpty(message = "first name should not be empty")
    @NotBlank(message = "first name should not contain white spaces")
    private String firstname;
    @NotEmpty(message = "last name should not be empty")
    @NotBlank(message = "last name should not contain white spaces")
    private String lastname;
    @Email(message = "email is not well formatted")
    @NotEmpty(message = "email should not be empty")
    @NotBlank(message = "email should not contain white spaces")
    private String email;
    @Size(min=8, message = "password should be minimum 8 characters long")
    @NotEmpty(message = "password should not be empty")
    @NotBlank(message = "password should not contain white spaces")
    private String password;
}
