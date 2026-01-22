package ru.skypro.homework.dto;

import lombok.Data;

@Data
public class AdDto {
    private Long pk;
    private Long author;   // id автора
    private String title;
    private String image;
    private Integer price;
}
