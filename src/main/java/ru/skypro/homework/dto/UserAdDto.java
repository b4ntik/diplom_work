package ru.skypro.homework.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAdDto {
    private Long id;
    private String title;
    private Integer price;
    private String imageUrl;
    private LocalDateTime createdAt;
}