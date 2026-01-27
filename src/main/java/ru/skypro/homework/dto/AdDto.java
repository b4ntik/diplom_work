package ru.skypro.homework.dto;

import lombok.Data;

@Data
public class AdDto {

    private Long author;   // id автора
    private String image;
    private Long pk;
    private Integer price;
    private String title;
}
