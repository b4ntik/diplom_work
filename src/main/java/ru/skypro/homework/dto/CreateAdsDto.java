package ru.skypro.homework.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//создание объявления
public class CreateAdsDto {
    @NotBlank(message = "Заголовок обязателен")
    @Size(min = 5, max = 100)
    private String title;

    @NotBlank(message = "Описание обязательно")
    @Size(min = 10, max = 1000)
    private String description;

    @NotNull(message = "Цена обязательна")
    @Positive
    private Integer price;

    // дополнительные поля
    private String phone;
    private String email;
}
