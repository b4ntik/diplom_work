package ru.skypro.homework.utils;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface CommentMapper {

    // Comment -> CommentDto
    @Mapping(source = "id", target = "pk")
    @Mapping(source = "author.id", target = "author")
    @Mapping(source = "author.firstName", target = "authorFirstName")
    @Mapping(source = "author.image", target = "authorImage")
    @Mapping(source = "createdAt", target = "createdAt")
    CommentDto toDto(Comment comment);

    // CommentDto -> Comment
    // из поля author (Long) восстанавливаем User с только id
    @Mapping(source = "author", target = "author")
    Comment toEntity(CommentDto dto);

    List<CommentDto> toDtoList(List<Comment> comments);

    //@Named("toTimestamp")
    default LocalDateTime toTimestamp(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime;
    }

    default User map(Long id) {
        if (id == null) {
            return null;
        }
        User user = new User();
        user.setId(id);
        return user;
    }
}
