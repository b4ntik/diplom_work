package ru.skypro.homework.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.RegisterDto;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.service.UserService;

import java.security.Principal;

@RestController
@RequestMapping
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody RegisterDto dto) {
        UserDto created = userService.register(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login() {
        // Basic Auth: успешная авторизация управляется Spring Security.
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/me")
    public ResponseEntity<UserDto> getMe(@AuthenticationPrincipal Principal principal) {
        UserDto dto = userService.getCurrentUser(principal);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/users/me")
    public ResponseEntity<UserDto> updateMe(@AuthenticationPrincipal Principal principal,
                                            @RequestBody UpdateUserDto dto) {
        UserDto updated = userService.updateUser(principal, dto);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/users/set_password")
    public ResponseEntity<Void> setPassword(@AuthenticationPrincipal Principal principal,
                                            @RequestBody NewPasswordDto dto) {
        userService.setPassword(principal, dto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/users/me/image")
    public ResponseEntity<String> updateAvatar(@AuthenticationPrincipal Principal principal,
                                               @RequestParam("image") MultipartFile image) {
        // Здесь можно реализовать сохранение файла и возврат пути
        String path = "/uploads/users/avatar_" + principal.getName() + ".jpg";
        userService.updateAvatar(principal, path);
        return ResponseEntity.ok(path);
    }
}
