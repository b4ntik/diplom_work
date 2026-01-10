package ru.skypro.homework.utils;

import ru.skypro.homework.dto.User;
import ru.skypro.homework.dto.UserExtendedResponseDto;
import ru.skypro.homework.dto.UserResponseDto;
import ru.skypro.homework.dto.UserUpdateDto;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDto toUserResponseDto(User user);

    UserExtendedResponseDto toUserExtendedResponseDto(User user);

    // Для маппинга списков
    List<UserResponseDto> toUserResponseDtoList(List<User> users);

    // Конвертация для обновления профиля
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "registrationDate", ignore = true)
    @Mapping(target = "ads", ignore = true)
    @Mapping(target = "comments", ignore = true)
    void updateUserFromDto(UserUpdateDto dto, @MappingTarget User user);
}