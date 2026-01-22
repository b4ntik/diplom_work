package ru.skypro.homework.dto;

import ru.skypro.homework.entity.Role;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String phone;
    private String image;
    private Role role;
}
