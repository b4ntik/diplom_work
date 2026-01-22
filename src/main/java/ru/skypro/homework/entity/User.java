package ru.skypro.homework.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; // логин / email

    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;
    private String phone;

    private String image; // путь к аватарке

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
}
