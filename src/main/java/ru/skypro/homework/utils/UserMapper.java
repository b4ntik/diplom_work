package ru.skypro.homework.utils;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.UserResponseDto;
import ru.skypro.homework.dto.UserUpdateDto;
import ru.skypro.homework.entity.Role;
import ru.skypro.homework.entity.User;

@Component
public class UserMapper {

    public UserResponseDto toUserResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        dto.setImage(user.getImage());
        dto.setRole(user.getRole().name());
        return dto;
    }

    public void updateUserFromDto(UserUpdateDto dto, User user) {
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getRole() != null) {
            user.setRole(Role.valueOf(dto.getRole().name()));
        }
    }
}
