package ru.skypro.homework.exceptions;

public class CommentDeleteForbiddenException extends RuntimeException {
    public CommentDeleteForbiddenException(String username, Long commentId) {
        super(String.format("Пользователь %s не может удалить комментарий %s",
                username, commentId));
    }
}
