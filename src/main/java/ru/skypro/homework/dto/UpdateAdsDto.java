package ru.skypro.homework.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//обновление объявлений
public class UpdateAdsDto {
    @Size(min = 5, max = 100)
    private String title;

    @Size(min = 10, max = 1000)
    private String description;

    @Positive
    private Integer price;

    private String phone;
    private String email;
}