package ru.skypro.homework.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserExtendedResponseDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String avatarUrl;
    private LocalDateTime registrationDate;
    private String role;
    private boolean enabled;

    // Дополнительная информация
    private int adsCount;
    private int commentsCount;
    private List<UserAdDto> recentAds;
    private LocalDateTime lastLogin;
}

