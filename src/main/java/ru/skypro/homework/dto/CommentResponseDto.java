package ru.skypro.homework.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    private Long id;
    private String text;
    private Long authorId;
    private String authorUsername;
    private String authorImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
