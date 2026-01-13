package ru.skypro.homework.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.client.HttpClientErrorException;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.exceptions.AdNotFoundException;
import ru.skypro.homework.exceptions.CommentNotFoundException;
import ru.skypro.homework.exceptions.UserNotFoundException;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.CommentService;
import ru.skypro.homework.service.UserService;
import ru.skypro.homework.exceptions.AccessDeniedException;

    @CrossOrigin(value = "http://localhost:3000")
    @RestController

    public class CommentsController {
        private final CommentService commentService;
        private final AdService adService;
        private final UserService userService;
        private final UserRepository userRepository;

        public CommentsController(CommentService commentService, AdService adService, UserService userService, UserRepository userRepository) {
            this.commentService = commentService;
            this.adService = adService;
            this.userService = userService;
            this.userRepository = userRepository;
        }

        //создание коммента
        @PostMapping("/ads/{id}/comments")
        public ResponseEntity<CommentResponseDto> createComment(
                @PathVariable Long adId,
                @Valid @RequestBody CreateCommentDto createCommentDto,
                Authentication authentication) {

            String username = authentication.getName();
            CommentResponseDto commentDto = commentService.createComment(adId, createCommentDto, username);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(commentDto);
        }

        //получение коммента
        @GetMapping("/ads/{id}/comments")
        public ResponseEntity<CommentsResponseDto> getComments(@PathVariable Long id) {
            CommentsResponseDto response = commentService.getCommentsResponse(id);
            return ResponseEntity.ok(response);
        }

        //удаление коммента
        @DeleteMapping("/{adId}/comments/{commentId}")
        public ResponseEntity<Void> deleteComment(
                @PathVariable Long adId,
                @PathVariable Long commentId,
                Authentication authentication) {

            try {
                // Получаем текущего пользователя
                String username = authentication.getName();
                User currentUser = userRepository.findByUsername(username)
                        .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

                // Вызываем метод сервиса
                commentService.deleteComment(adId, commentId, currentUser);

                return ResponseEntity.noContent().build();

            } catch (UserNotFoundException | AdNotFoundException | CommentNotFoundException e) {
                return ResponseEntity.notFound().build();
            } catch (AccessDeniedException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } catch (Exception e) {
                //log.error("Ошибка при удалении комментария", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        //изменение коммента
        @PatchMapping("ads/{adId}/comments/{commentId}")
        public ResponseEntity<?> updateComment(
                @PathVariable Long adId,
                @PathVariable Long commentId,
                @Valid @RequestBody CommentUpdateRequest updateRequest,
                @AuthenticationPrincipal User currentUser,
                BindingResult result){
            //проверка значений на корректность
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body("Проверьте значения");
            }
            //пытаемся обновить комментарий
            try{
                CommentResponseDto updateComment = commentService.updateComment(adId, commentId, updateRequest, currentUser);
                return ResponseEntity.ok(updateComment);
            } catch (HttpClientErrorException.NotFound e){
                return ResponseEntity.notFound().build();
            }
            catch (AccessDeniedException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            catch (Exception e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

}
