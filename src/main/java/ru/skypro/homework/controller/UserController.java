package ru.skypro.homework.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.RegisterDto;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.service.UserService;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getMe(Principal principal) {
        log.info("получаем информацию о юзере");
        // Просто используем Principal без аннотации
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDto dto = userService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/me")
    public ResponseEntity<UserDto> updateMe(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateUserDto dto) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("Обновление информации пользователя: {}", userDetails.getUsername());
        UserDto updated = userService.updateUser(userDetails.getUsername(), dto);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/set_password")
    public ResponseEntity<Void> setPassword(
            Principal principal,
            @RequestBody NewPasswordDto dto) {

        if (principal == null) {
            log.error("Пользователь не аутентифицирован");
            return ResponseEntity.status(401).build();
        }

        log.info("Смена пароля для пользователя: {}", principal.getName());
        log.info("Текущий пароль: [скрыто], новый пароль: [скрыто]");

        userService.setPassword(principal.getName(), dto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value ="/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateAvatar(
            Principal principal,
            @RequestParam("image") MultipartFile image) {

        if (principal == null) {
            log.error("Пользователь не аутентифицирован");
            return ResponseEntity.status(401).body("User not authenticated");
        }

        log.info("Обновление аватара пользователя: {}", principal.getName());
        log.info("Размер файла: {} байт", image.getSize());
        log.info("Тип файла: {}", image.getContentType());
        log.info("Имя файла: {}", image.getOriginalFilename());

        try {
            String path = userService.updateAvatar(principal.getName(), image);
            return ResponseEntity.ok(path);
        } catch (Exception e) {
            log.error("Ошибка при обновлении аватара: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error updating avatar: " + e.getMessage());
        }
    }
}
