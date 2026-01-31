package ru.skypro.homework.utils;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    // Comment -> CommentDto
    // из поля User author берём id и кладём в поле author (Long)
    @Mapping(source = "author.id", target = "author")
    CommentDto toDto(Comment comment);

    // CommentDto -> Comment
    // из поля author (Long) восстанавливаем User с только id
    @Mapping(source = "author", target = "author")
    Comment toEntity(CommentDto dto);

    @Mapping(source = "author.firstName", target = "authorFirstName")

    @Mapping(source = "author.image", target = "authorImage")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "toTimestamp")

    List<CommentDto> toDtoList(List<Comment> comments);

    @Named("toTimestamp")
    default Long toTimestamp(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.atZone(java.time.ZoneId.systemDefault())
                .toInstant().toEpochMilli();
    }
    // Простейший конвертер Long -> User для обратного маппинга
    // (используется автоматически в случае отсутствия другого маппера)
    default User map(Long id) {
        if (id == null) {
            return null;
        }
        User user = new User();
        user.setId(id);
        return user;
    }
}
