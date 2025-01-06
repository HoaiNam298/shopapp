package com.project.shopapp.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.model.Comment;
import com.project.shopapp.model.Product;
import com.project.shopapp.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse {

    @JsonProperty("user")
    private UserResponse userResponse;

    @JsonProperty("content")
    private String content;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    public static CommentResponse fromComment(Comment comment) {
        return CommentResponse
                .builder()
                .userResponse(UserResponse.fromUser(comment.getUser()))
                .content(comment.getContent())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
