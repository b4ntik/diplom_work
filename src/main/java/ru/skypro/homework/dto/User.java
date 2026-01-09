package ru.skypro.homework.dto;

import lombok.Getter;

import javax.validation.constraints.Size;

public class User {
    private String username;
    private String password;
    private String firstName;
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
