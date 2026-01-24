package ru.skypro.homework.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.RegisterDto;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entity.Role;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exceptions.UserNotFoundException;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.utils.UserMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    // Конфигурация путей
    private final String uploadDir = "uploads/users/";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserDto register(RegisterDto dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("User already exists");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole() != null ? dto.getRole() : Role.USER);
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    public UserDto getCurrentUser(String username) {

        log.debug("Поиск пользователя по username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toDto(user);
    }
    public UserDto getCurrentUser(Long userId) {

        log.debug("Поиск пользователя по ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден с ID: " + userId));

        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto updateUser(String username, UpdateUserDto updateDto) {
        log.debug("Обновление пользователя: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + username));

        // Обновляем поля, если они не null
        if (updateDto.getFirstName() != null) {
            user.setFirstName(updateDto.getFirstName());
        }

        if (updateDto.getLastName() != null) {
            user.setLastName(updateDto.getLastName());
        }

        if (updateDto.getPhone() != null) {
            user.setPhone(updateDto.getPhone());
        }

        User savedUser = userRepository.save(user);
        log.debug("Пользователь обновлен: {}", savedUser.getUsername());

        return userMapper.toDto(savedUser);
    }


    @Transactional
    public void setPassword(String username, NewPasswordDto dto) {
        log.debug("Смена пароля для пользователя: {}", username);

        // Находим пользователя
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + username));

        // Проверяем текущий пароль
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            log.warn("Неверный текущий пароль для пользователя: {}", username);
            throw new RuntimeException("Неверный текущий пароль");
        }

        // Проверяем что новый пароль отличается от старого
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new RuntimeException("Новый пароль должен отличаться от старого");
        }

        // Хешируем и сохраняем новый пароль
        String encodedNewPassword = passwordEncoder.encode(dto.getNewPassword());
        user.setPassword(encodedNewPassword);
        userRepository.save(user);

        log.info("Пароль успешно изменен для пользователя: {}", username);
    }

    // для обновления аватара
    @Transactional
    public String updateAvatar(String username, MultipartFile image) {
        log.debug("Обновление аватара для пользователя: {}", username);

        // 1. Находим пользователя
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // 2. Сохраняем файл
        String filePath = saveImage(image, username);

        // 3. Обновляем путь в базе
        user.setImage(filePath);
        userRepository.save(user);

        log.info("Аватар обновлен для пользователя: {}, путь: {}", username, filePath);

        return filePath;
    }
    //дополнительный метод для сохранения аватара
    private String saveImage(MultipartFile image, String username) {
        try {
            // Создаем директорию если не существует
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Генерируем уникальное имя файла
            String originalFilename = image.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String fileName = "avatar_" + username + "_" + UUID.randomUUID() + fileExtension;

            // Сохраняем файл
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(image.getInputStream(), filePath);

            // Возвращаем относительный путь
            return uploadDir + fileName;

        } catch (IOException e) {
            log.error("Ошибка сохранения файла: {}", e.getMessage());
            throw new RuntimeException("Failed to save image: " + e.getMessage());
        }
    }
    //туда же - дополнительный метод для автара
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".jpg"; // значение по умолчанию
        }
        return filename.substring(filename.lastIndexOf("."));
    }


}