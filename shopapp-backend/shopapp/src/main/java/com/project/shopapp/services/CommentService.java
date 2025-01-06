package com.project.shopapp.services;

import com.project.shopapp.dtos.CommentDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.model.Comment;
import com.project.shopapp.response.CommentResponse;

import java.util.List;

public interface CommentService {
    void insertComment(CommentDTO commentDTO) throws Exception;
    void deleteComment(Long commentId);
    void updateComment(Long id, CommentDTO commentDTO) throws DataNotFoundException;
    List<CommentResponse> getCommentsByUserAndProduct(Long userId, Long productId);
    List<CommentResponse> getCommentsByProduct(Long productId);
}
