package ru.skypro.homework.dto;

import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserUpdateDto {
    @Getter
    @Email(message = "Некорректный email")
    private String email;

    @Getter
    @Size(min = 2, max = 50, message = "Имя должно содержать от 2 до 50 символов")
    private String firstName;

    @Getter
    @Size(min = 2, max = 50, message = "Фамилия должна содержать от 2 до 50 символов")
    private String lastName;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Некорректный формат телефона")
    private String phone;

    private String imageUrl;


    public String getUserPhone() { return phone;
    }

    public Role getRole() { return role;
    }

    public String getUserImage() { return imageUrl; }
    }

