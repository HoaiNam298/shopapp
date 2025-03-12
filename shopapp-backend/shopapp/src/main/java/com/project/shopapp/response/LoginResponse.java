package com.project.shopapp.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.model.Role;
import com.project.shopapp.model.User;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {

    private long id;

    @JsonProperty("message")
    private String message;

    @JsonProperty("token")
    private String token;

    @JsonProperty("token_type")
    private String tokenType = "Bearer";

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("roles")
    private List<String> roles;

    @JsonProperty("username")
    private String username;
}
