package ru.skypro.homework.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewPasswordDto {
    @NotBlank(message = "Текущий пароль обязателен")
    private String currentPassword;

    @NotBlank(message = "Новый пароль обязателен")
    @Size(min = 8, message = "Новый пароль должен быть не менее 8 символов")
    private String newPassword;
}
