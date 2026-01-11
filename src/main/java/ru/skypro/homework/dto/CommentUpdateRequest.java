package ru.skypro.homework.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;



public class CommentUpdateRequest {

    @NotBlank(message = "Комментарий не может быть пустым")
    @Size(min = 1, max = 2000, message = "Комментарий может содержать от 1 до 2000 символов")
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
