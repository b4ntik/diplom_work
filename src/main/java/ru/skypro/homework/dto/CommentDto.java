package ru.skypro.homework.dto;

import lombok.Data;

@Data
public class CommentDto {
    private Long pk;
    private Long author;
    private String authorFirstName;
    private String authorLastName;
    private String authorImage;
    private String createdAt;
    private String text;
}