package ru.skypro.homework.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.service.CommentService;

@CrossOrigin(value = "http://localhost:3000")
@RestController
public class CommentsController {

    private final CommentService commentService;

    public CommentsController(CommentService commentService) {
        this.commentService = commentService;
    }

    // GET /ads/{id}/comments - Получение комментариев объявления
    @GetMapping("/ads/{id}/comments")
    public ResponseEntity<CommentsDto> getComments(@PathVariable("id") Integer id) {
        try {
            CommentsDto comments = commentService.getComments(id);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /ads/{id}/comments - Добавление комментария к объявлению
    @PostMapping("/ads/{id}/comments")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable("id") Integer id,
            @RequestBody CreateOrUpdateCommentDto createOrUpdateCommentDto,
            Authentication authentication) {

        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).build();
            }

            String username = authentication.getName();
            CommentDto comment = commentService.addComment(id, createOrUpdateCommentDto, username);
            return ResponseEntity.ok(comment);
        } catch (Exception e) {
            if (e.getMessage().contains("не найден")) {
                return ResponseEntity.status(404).build();
            }
            return ResponseEntity.status(401).build();
        }
    }

    // DELETE /ads/{adId}/comments/{commentId} - Удаление комментария
    @DeleteMapping("/ads/{adId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable("adId") Integer adId,
            @PathVariable("commentId") Integer commentId,
            Authentication authentication) {

        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).build();
            }

            String username = authentication.getName();
            commentService.deleteComment(adId, commentId, username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            String message = e.getMessage();
            if (message.contains("не найден")) {
                return ResponseEntity.status(404).build();
            }
            if (message.contains("AccessDenied") || message.contains("прав")) {
                return ResponseEntity.status(403).build();
            }
            return ResponseEntity.status(401).build();
        }
    }

    // PATCH /ads/{adId}/comments/{commentId} - Обновление комментария
    @PatchMapping("/ads/{adId}/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable("adId") Integer adId,
            @PathVariable("commentId") Integer commentId,
            @RequestBody CreateOrUpdateCommentDto createOrUpdateCommentDto,
            Authentication authentication) {

        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).build();
            }

            String username = authentication.getName();
            CommentDto updatedComment = commentService.updateComment(
                    adId, commentId, createOrUpdateCommentDto, username);
            return ResponseEntity.ok(updatedComment);
        } catch (Exception e) {
            String message = e.getMessage();
            if (message.contains("не найден")) {
                return ResponseEntity.status(404).build();
            }
            if (message.contains("AccessDenied") || message.contains("прав")) {
                return ResponseEntity.status(403).build();
            }
            return ResponseEntity.status(401).build();
        }
    }
}