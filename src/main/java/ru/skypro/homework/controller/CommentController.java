package ru.skypro.homework.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CommentListResponse;
import ru.skypro.homework.dto.CreateOrUpdateCommentDto;
import ru.skypro.homework.service.CommentService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://127.0.0.1:3000"
}, allowCredentials = "true")
@RequiredArgsConstructor
@RequestMapping("/ads")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{adId}/comments")
    public ResponseEntity<CommentListResponse> getCommentsForAd(
            @PathVariable Long adId) {

        log.info("Получение комментариев для объявления ID: {}", adId);

        List<CommentDto> comments = commentService.getCommentsByAdId(adId);

        CommentListResponse response = new CommentListResponse();
        response.setCount(comments.size());
        response.setResults(comments);

        log.info("Найдено комментариев: {}", comments.size());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{adId}/comments")
    public ResponseEntity<CommentDto> createComment(
            @PathVariable Long adId,
            @Valid @RequestBody CreateOrUpdateCommentDto commentDto,
            Principal principal) {

        log.info("=== НАЧАЛО СОЗДАНИЯ КОММЕНТАРИЯ ===");
        log.info("adId из пути: {}", adId);
        log.info("Principal: {}", principal);
        log.info("Имя пользователя: {}", principal != null ? principal.getName() : "null");
        log.info("Текст комментария: {}", commentDto.getText());

        if (principal == null) {
            log.error("Пользователь не аутентифицирован при создании комментария");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("Создание комментария для объявления ID: {}, пользователь: {}",
                adId, principal.getName());
        log.info("Текст комментария: {}", commentDto.getText());

        try {
            CommentDto createdComment = commentService.createComment(
                    adId, commentDto.getText(), principal.getName());

            log.info("=== КОММЕНТАРИЙ УСПЕШНО СОЗДАН ===");
            log.info("ID комментария: {}", createdComment.getPk());

            return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);

        } catch (RuntimeException e) {
            log.error("=== ОШИБКА СОЗДАНИЯ КОММЕНТАРИЯ ===");
            log.error("Сообщение: {}", e.getMessage());
            log.error("Стек трейс:", e);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment( @PathVariable Long adId,
                                               @PathVariable Long commentId,
                                               Principal principal) {

        if (principal == null) {
            log.error("Пользователь не аутентифицирован при удалении комментария");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("Удаление комментария: adId={}, commentId={}, user={}",
                adId, commentId, principal.getName());

        try {
            commentService.deleteComment(adId, commentId, principal.getName());
            log.info("Комментарий {} успешно удален", commentId);
            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            log.error("Ошибка при удалении комментария: {}", e.getMessage());

            if (e.getMessage().contains("не найден") || e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else if (e.getMessage().contains("нет прав") || e.getMessage().contains("not authorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
    }
    @PatchMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long adId,
            @PathVariable Long commentId,
            @Valid @RequestBody CreateOrUpdateCommentDto updateDto,
            Principal principal) {

        if (principal == null) {
            log.error("Пользователь не аутентифицирован при редактировании комментария");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("Редактирование комментария: adId={}, commentId={}, user={}",
                adId, commentId, principal.getName());
        log.info("Новый текст: {}", updateDto.getText());

        try {
            CommentDto updatedComment = commentService.updateComment(
                    adId, commentId, updateDto.getText(), principal.getName());

            log.info("Комментарий {} успешно обновлен", commentId);
            return ResponseEntity.ok(updatedComment);

        } catch (RuntimeException e) {
            log.error("Ошибка при редактировании комментария: {}", e.getMessage());

            if (e.getMessage().contains("не найден") || e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else if (e.getMessage().contains("нет прав") || e.getMessage().contains("not authorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
    }
}