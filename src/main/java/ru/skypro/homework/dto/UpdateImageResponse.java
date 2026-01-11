package ru.skypro.homework.dto;

public class UpdateImageResponse {
    private String imageUrl;

    public UpdateImageResponse(String imageUrl) {
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}