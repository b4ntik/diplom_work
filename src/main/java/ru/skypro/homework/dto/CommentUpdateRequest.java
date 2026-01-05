package ru.skypro.homework.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateRequest {

    //@NotBlank(message = "Комментарий не может быть пустым");
    //@Size(min = 1, max = 2000, message = "Комментарий может содержать от 1 до 2000 символов");
    private String text;
    private LocalDateTime dateUpdate;

}
