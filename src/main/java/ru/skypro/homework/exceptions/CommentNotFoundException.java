package ru.skypro.homework.exceptions;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(Long commentId, Long adId) {
        super(String.format("Комментарий с ID %s не найден в объявлении %s", commentId, adId));
    }
}
