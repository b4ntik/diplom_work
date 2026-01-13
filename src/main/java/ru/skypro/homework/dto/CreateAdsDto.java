package ru.skypro.homework.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

//создание объявления
public class CreateAdsDto {
    @NotBlank(message = "Заголовок обязателен")
    @Size(min = 5, max = 100)
    private String title;

    @NotBlank(message = "Описание обязательно")
    @Size(min = 10, max = 1000)
    private String description;

    @NotNull(message = "Цена обязательна")
    @Positive
    private Integer price;

    // дополнительные поля
    private String phone;
    private String email;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
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
}
