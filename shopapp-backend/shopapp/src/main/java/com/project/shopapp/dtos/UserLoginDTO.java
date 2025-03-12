package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.model.Role;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("fullname")
    private String fullName;

    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Password can not be blank")
    private String password;

    @JsonProperty("facebook_account_id")
    private String facebookAccountId;

    @JsonProperty("google_account_id")
    private String googleAccountId;

    @Min(value = 1, message = "You must enter role's Id")
    @JsonProperty("role_id")
    private Long roleId;
}
