package ru.skypro.homework.utils;


import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.CommentResponseDto;
import ru.skypro.homework.dto.CommentsResponseDto;
import ru.skypro.homework.dto.CreateCommentDto;
import ru.skypro.homework.entity.Comment;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapper {
    public CommentResponseDto toDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        CommentResponseDto dto = new CommentResponseDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setCreatedAt(comment.getCreatedAt());

//        // Информация об авторе
//        User author = comment.getAuthor();
//        if (author != null) {
//            dto.setAuthorId(author.getId());
//            dto.setAuthorUsername(author.getUsername());
//            dto.setAuthorImage(author.getImage() != null ?
//                    author.getImage() : "/images/default-avatar.jpg");
//        }

        // Информация об объявлении (если нужно)
        if (comment.getAd() != null) {
            dto.setId(comment.getAd().getId());
        }

        return dto;
    }

    public List<CommentResponseDto> toDtoList(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return List.of();
        }

        return comments.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Comment toEntity(CreateCommentDto dto) {
        if (dto == null) {
            return null;
        }

        Comment comment = new Comment();
        comment.setText(dto.getText());
        // author, ad, createdAt устанавливаются в сервисе

        return comment;
    }

    /**
     * Обновление существующего комментария из DTO
     */
    public void updateCommentFromDto(Comment comment, CreateCommentDto dto) {
        if (comment == null || dto == null) {
            return;
        }

        // Обновляем только текст
        comment.setText(dto.getText());
    }
    public CommentsResponseDto toCommentsResponseDto(List<Comment> comments) {
        CommentsResponseDto response = new CommentsResponseDto();
        response.setCount(comments.size());
        response.setResults(toDtoList(comments));
        return response;
    }
}