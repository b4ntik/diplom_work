package ru.skypro.homework.controller;

import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.UserUpdateDto;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.service.UserService;

import java.io.IOException;
import java.util.Map;


@CrossOrigin(value = "http://localhost:3000")
@RestController
//@RequestMapping("/users")

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/set_password")
    public ResponseEntity<?> setPassword(
            @RequestBody Map<String, String> password,
            Authentication authentication) {

        String currentPassword = password.get("currentPassword");
        String newPassword = password.get("newPassword");

        if (currentPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().body("Проверьте пароли");
        }

        String username = authentication.getName();
        User user = userService.getUserByUsername(username);

        boolean success = userService.changePassword(user.getId(), currentPassword, newPassword);

        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный пароль");
        }
    }

@GetMapping("/users/me")
public User getUser(Authentication authentication) {
    //log.info("Запрос информации о пользователе: {}", authentication.getName());

    User user = userService.getCurrentUser(authentication);

    //log.debug("Информация о пользователе {} успешно получена", authentication.getName());

    return user;

}



    @PatchMapping("/me")
    public ResponseEntity<?> updateUserInfo(
            @Valid @RequestBody UserUpdateDto updateDto,
            BindingResult result,
            Authentication authentication) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        // Получаем текущего пользователя из authentication
        String username = authentication.getName();
        User currentUser = userService.getUserByUsername(username);

        boolean isUpdated = userService.partialUpdateUser(currentUser.getId(), updateDto);
        if (isUpdated) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    @PatchMapping("/me/image")
    public ResponseEntity<?> updateUserImage(
            @RequestParam("image") MultipartFile imageFile,
            Authentication authentication) throws IOException {
        userService.updateUserImage(imageFile);
        return ResponseEntity.ok().build();

    }
}
