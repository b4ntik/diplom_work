package ru.skypro.homework.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long pk;
    private Long author;
    private String authorFirstName;
    //private String authorLastName;
    //private String authorImage;
    private LocalDateTime createdAt;
    private String text;
}