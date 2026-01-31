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
import java.util.List;


@Slf4j
@RestController
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
}