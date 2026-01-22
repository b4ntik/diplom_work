package ru.skypro.homework.utils;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.entity.User;

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
