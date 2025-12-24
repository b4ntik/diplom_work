package ru.skypro.homework.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.client.HttpClientErrorException;
import ru.skypro.homework.dto.UserResponseDto;
import ru.skypro.homework.service.CommentService;


import java.nio.file.AccessDeniedException;
import java.util.Map;


    @Slf4j
    @CrossOrigin(value = "http://localhost:3000")
    @RestController
    @RequiredArgsConstructor
    public class CommentsController {
        private final CommentService commentService;

        //создание коммента
        @PostMapping("/ads/{id}/comments")
        public ResponseEntity<?> setPassword(@RequestBody Map<String, String> password) {
            String currentPassword = password.get("currentPassword");
            String newPassword = password.get("newPassword");
            if (currentPassword == null || newPassword == null) {
                return ResponseEntity.badRequest().body("Проверьте пароли");
            }
            User currentUser = getCurrentUser();
            if (userService.changePassword(currentUser.getUserId(), currentPassword, newPassword)) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный пароль");
            }
        }

        //получение коммента
        @GetMapping("/ads/{id}/comments")
        public ResponseEntity<?> userInfo(@AuthenticationPrincipal UserDetails userDetails) {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            User currentUser = (User) userDetails;
            if (!userService.userExists(currentUser.getUsername())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            UserResponseDto userInfo = userService.getUserInfo(curretUser.getUsername());

            return ResponseEntity.ok(userInfo);
        }

        //удаление коммента
        @DeleteMapping("/ads/{adId}/comments/{commentsId}")
        public ResponseEntity<?> deleteComment(@PathVariable int adId,
                                               @PathVariable int commentsId,
                                               @AuthenticationPrincipal UserDetails userDetails) {
            try {
                if (userDetails == null) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }

                User currentUser = (User) userDetails;

                Ad ad = adService.getAdById(adId);
                if (ad == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }
                Comment comment = commentsService.getCommetById(commentId);
                if (comment == null || !comment.getAd().getId().equals(adId)) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }
                if (!commentService.canDeleteComment(commentId, currentUser)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
                commentService.deleteComment(commentId);

                return ResponseEntity.noContent().build();
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Ошибка при удалении комментария");
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
                CommentResponseDto updateComment = commentServie.updateComment(
                        adId, commentId, updateRequest, currentUser);
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
