package ru.skypro.homework.dto;


import java.time.LocalDateTime;

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
            return description.substring(0, 100) + "..";
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public String getAuthorFirstName() {
        return authorFirstName;
    }

    public void setAuthorFirstName(String authorFirstName) {
        this.authorFirstName = authorFirstName;
    }

    public String getAuthorLastName() {
        return authorLastName;
    }

    public void setAuthorLastName(String authorLastName) {
        this.authorLastName = authorLastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
