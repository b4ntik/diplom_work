package ru.skypro.homework.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CreateOrUpdateCommentDto {

    @NotBlank(message = "Текст комментария обязателен")
    @Size(min = 8, max = 64, message = "Текст должен быть от 8 до 64 символов")
    private String text;

    // Конструкторы
    public CreateOrUpdateCommentDto() {
    }

    public CreateOrUpdateCommentDto(String text) {
        this.text = text;
    }

    // Геттеры и сеттеры
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "CreateOrUpdateCommentDto{" +
                "text='" + text + '\'' +
                '}';
    }
}