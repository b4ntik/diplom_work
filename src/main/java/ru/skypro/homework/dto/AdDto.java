package ru.skypro.homework.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdDto {

    private Long author;   // id автора
    private String image;
    private Long pk;
    private Integer price;
    private String title;
}
