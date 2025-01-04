package com.project.shopapp.controller;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.dtos.CommentDTO;
import com.project.shopapp.model.Comment;
import com.project.shopapp.model.User;
import com.project.shopapp.response.CategoryResponse;
import com.project.shopapp.services.CommentService;
import com.project.shopapp.ultils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("")
    public ResponseEntity<?> getAll(
            @RequestParam("user_id") long userId,
            @RequestParam("product_id") long productId
    ){
        List<Comment> comments = commentService.getCommentsByUserAndProduct(userId, productId);
        return ResponseEntity.ok(comments);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PostMapping("")
    public ResponseEntity<?> insertComment(
            @Valid @RequestBody CommentDTO commentDTO
    ){
        try {
            User loginUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (loginUser.getId() != commentDTO.getUserId()) {
                return ResponseEntity.badRequest().body("You cannot comment as another user");
            }
            commentService.insertComment(commentDTO);
            return ResponseEntity.ok("Insert comment successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during comment insertion");
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentDTO commentDTO,
            Authentication authentication
    ){
        try {
            commentService.updateComment(id, commentDTO);
            return ResponseEntity.ok("Update comment successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during comment update");
        }
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteCategory(@PathVariable Long id){
//        categoryService.getCategoryById(id);
//        return ResponseEntity.ok(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_CATEGORY_SUCCESSFULLY, id));
//    }
}
