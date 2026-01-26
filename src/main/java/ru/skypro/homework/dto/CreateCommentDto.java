package ru.skypro.homework.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CreateCommentDto {
    @NotBlank(message = "Текст комментария не может быть пустым")
    @Size(min = 1, max = 1000, message = "Комментарий должен содержать от 1 до 1000 символов")
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}