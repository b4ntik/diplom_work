package ru.skypro.homework.entity;

import lombok.Getter;
import ru.skypro.homework.dto.Role;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Getter
    @Enumerated(EnumType.STRING)
    private Role role;

    private String avatarUrl;

    public Object getId() { return id;
    }

//    public Object getRole() {
//    }

//    public String getUsername() { return username;
//    }
}
