package ru.skypro.homework.dto;

public class UserResponseDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String image;
    private String role;

    // Конструкторы
    public UserResponseDto() {}

    public UserResponseDto(Long id, String email, String firstName, String lastName,
                           String phone, String image, String role) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.image = image;
        this.role = role;
    }
    // Геттеры и сеттеры (вручную)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getImage() {
        return image != null ? image : "/images/default-avatar.jpg";
    }
    public void setImage(String image) { this.image = image; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
