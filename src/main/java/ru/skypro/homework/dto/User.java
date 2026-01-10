package ru.skypro.homework.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;

public class User {
    @Getter
    private Long id;
    @Getter
    private String username;
    private String password;
    @Getter
    private String firstName;
    @Getter
    private String lastName;
    private String phone;
    @Getter
    private Role role;

    public void setEmail(String email) {
    }

    public void setFirstName(@Size(min = 2, max = 50, message = "Имя должно содержать от 2 до 50 символов") String firstName) {
    }

    public void setLastName(@Size(min = 2, max = 50, message = "Фамилия должна содержать от 2 до 50 символов") String lastName) {
    }

    public void setUserPhone(String userPhone) {
    }

    public void setRole(Role role) {
    }

    public void setUserImage(String userImage) {
    }

}

