package ru.skypro.homework.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.RegisterDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.service.UserService;

@RestController
@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://127.0.0.1:3000"
}, allowCredentials = "true")
@RequestMapping()
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
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
}