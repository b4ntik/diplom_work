package ru.skypro.homework.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class CommentExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleAdNotFound(RuntimeException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setError("Not Found");
        error.setMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCommentNotFound(CommentNotFoundException ex) {
        ErrorResponse error = new ErrorResponse();
                error.setError("COMMENT_NOT_FOUND");
                error.setError(ex.getMessage());
                error.setTimestamp(LocalDateTime.now());
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(CommentDeleteForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleCommentDeleteForbidden(
            CommentDeleteForbiddenException ex) {
        ErrorResponse error = new ErrorResponse();
                error.setError("FORBIDDEN_DELETE");
                error.setError(ex.getMessage());
                error.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }


    @Data
    public static class ErrorResponse {
        private String error;
        private String message;
        private LocalDateTime timestamp;
    }
}