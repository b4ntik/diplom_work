package ru.skypro.homework.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdsDto {
    private Long id;
    private String title;
    private String description;
    private Integer price;
    private String authorUsername;
    private String authorFirstName;
    private String authorLastName;
    private String phone;
    private String email;
    private String imageUrl;
    private LocalDateTime createdAt;

    public String getDescription() {
        if (description != null) {
            return description;
        }
        if (description != null && description.length() > 100) {
            return description.substring(0, 100) + "...";
        }
        return description;
    }

    public String getImageUrl() {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return "/images/default-ad.jpg";
        }
        return imageUrl;
    }

    public void setAuthorId(Long id) {
    }

    public void setAuthorName(String username) {
    }
}
