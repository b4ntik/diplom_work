package ru.skypro.homework.service;

import org.springframework.data.crossstore.ChangeSetPersister;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.exceptions.UnauthorizedException;
import ru.skypro.homework.exceptions.UserNotFoundException;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.utils.UserMapper;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.entity.Role;
import ru.skypro.homework.dto.UserUpdateDto;
import ru.skypro.homework.dto.UserResponseDto;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ImageStorageService  imageStorageService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, ImageStorageService imageStorageService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.imageStorageService = imageStorageService;
        this.passwordEncoder = passwordEncoder;
    }

    public User updateUserInfo(Long userId, UserUpdateDto updateDto, User currentUser) throws AccessDeniedException, ChangeSetPersister.NotFoundException {

        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        // Частичное обновление полей (только если они не null в DTO)
        if (updateDto.getEmail() != null) {
            userToUpdate.setEmail(updateDto.getEmail());
        }
        if (updateDto.getFirstName() != null) {
            userToUpdate.setFirstName(updateDto.getFirstName());
        }
        if (updateDto.getLastName() != null){
            userToUpdate.setLastName(updateDto.getLastName());
        }
        if (updateDto.getPhone() != null){
            userToUpdate.setPhone(updateDto.getPhone());
        }
        if (updateDto.getRole() != null && !updateDto.getRole().equals(userToUpdate.getRole())){
            if(!currentUser.getRole().equals(Role.ADMIN)){
                throw new AccessDeniedException("Только админ может менять роль!");
            }
            userToUpdate.setRole(
                    Role.valueOf(updateDto.getRole().name())
            );
        }

        return userRepository.save(userToUpdate);
    }

    public boolean userExists(String username) {
        //проверка, что username не null и не пустой
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return userRepository.existsByUsername(username.trim());
    }
    public UserResponseDto getUserInfo(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        "Пользователь с именем '" + username + "' не найден"
                ));

        return userMapper.toUserResponseDto(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        "Пользователь " + username + " не найден"
                ));
    }
    public boolean partialUpdateUser(Long userId, UserUpdateDto updateDto) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

            // Обновляем только переданные поля
            userMapper.updateUserFromDto(updateDto, user);
            userRepository.save(user);

            return true;

        } catch (UserNotFoundException e) {
            //log.error("Пользователь не найден: {}", userId, e);
            return false;
        } catch (Exception e) {
            //  log.error("Ошибка при обновлении пользователя: {}", userId, e);
            return false;
        }
    }
    public void updateUserImage(MultipartFile imageFile) throws IOException {
        // Получаем текущего пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        // Удаляем старое изображение если есть
        if (user.getImage() != null && !user.getImage().isEmpty()) {
            try {
                imageStorageService.deleteImage(user.getImage());
            } catch (IOException e) {
                // Логируем ошибку, но продолжаем
                System.err.println("Не удалось удалить старое изображение: " + e.getMessage());
            }
        }

        String originalFilename = imageFile.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = "user_" + user.getId() + "_" + System.currentTimeMillis() + extension;

        // Сохраняем изображение
        String imagePath = imageStorageService.saveImage(imageFile, filename, "users");
        user.setImage(imagePath);

        // Сохраняем пользователя
        userRepository.save(user);
    }

    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        // Проверяем текущий пароль
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;
        }

        // Обновляем пароль
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return true;
    }
    public User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Пользователь не аутентифицирован");
        }

        String username = authentication.getName();
        return getUserByUsername(username);
    }
}
