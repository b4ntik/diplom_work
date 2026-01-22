package ru.skypro.homework.utils;


import org.mapstruct.Mapper;
import org.mapstruct.Named;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserDto dto);

    // Converter: User -> Long (id)
    //Long map(User user);
    default Long map(String value) {
        return value == null ? null : Long.valueOf(value);
    }
    // Можно оставить защитную версию, если нужен null-safe вариант
    default Long mapOrNull(User user) {
        return user != null ? user.getId() : null;
    }
}