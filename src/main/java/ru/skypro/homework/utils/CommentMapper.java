package ru.skypro.homework.utils;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.CommentResponseDto;
import ru.skypro.homework.dto.CreateCommentDto;
import ru.skypro.homework.entity.Comment;

@Component
public class CommentMapper {

    private final ModelMapper modelMapper;

    public CommentMapper() {
        this.modelMapper = new ModelMapper();
        configureMappings();
    }

    private void configureMappings() {
        modelMapper.createTypeMap(Comment.class, CommentResponseDto.class)
                .addMapping(src -> src.getAuthor().getId(), CommentResponseDto::setAuthorId)
                .addMapping(src -> src.getAuthor().getUsername(), CommentResponseDto::setAuthorUsername);

    }

    public CommentResponseDto toDto(Comment comment) {
        return modelMapper.map(comment, CommentResponseDto.class);
    }

    public Comment toEntity(CreateCommentDto dto) {
        Comment comment = new Comment();
        comment.setText(dto.getText());
        return comment;
    }
}