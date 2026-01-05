package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.UserResponseDto;
import ru.skypro.homework.dto.UserUpdateDto;
import ru.skypro.homework.service.UserService;

import javax.validation.Valid;
import java.util.Map;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/users/set_password")
    public ResponseEntity<?> setPassword(@RequestBody Map<String, String> password) {
        String currentPassword = password.get("currentPassword");
        String newPassword = password.get("newPassword");
        if (currentPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().body("Проверьте пароли");
        }
        User currentUser = getCurrentUser();
        if (userService.changePassword(currentUser.getUserId(), currentPassword, newPassword)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный пароль");
        }
    }

    @GetMapping("/users/me")
    public ResponseEntity<?> userInfo(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User currentUser = (User) userDetails;
        if (!userService.userExists(currentUser.getUsername())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        UserResponseDto userInfo = userService.getUserInfo(curretUser.getUsername());

        return ResponseEntity.ok(userInfo);
    }

    @PatchMapping("/users/me")
    public ResponseEntity<?> updateUserInfo(@Valid @RequestBody UserUpdateDto updateDto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        User currentUser = getCurrentAuthenticatedUser();
        boolean isUpdated = userService.partialUpdateUser(currentUser.getUserId(), updateDto);
        if (isUpdated) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PatchMapping("/users/me/image")
    public ResponseEntity<?> updateUserImage(@Valid @RequestBody UserUpdateDto updateDto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        User currentUser = getCurrentAuthenticatedUser();
        boolean isUpdated = userService.partialUpdateUser(currentUser.getUserId(), updateDto);
        if (isUpdated) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }
}
