package ru.skypro.homework.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;



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

    @Getter
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Некорректный формат телефона")
    private String phone;

    @Getter
    private String imageUrl;

    @Getter
    @JsonIgnore
    private Role role;

//    public Role getRole() {
//        return role;
//    }
}

