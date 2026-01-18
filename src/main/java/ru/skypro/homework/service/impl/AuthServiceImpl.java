package ru.skypro.homework.service.impl;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.RegisterDto;

import ru.skypro.homework.entity.Role;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AuthService;

import java.time.LocalDateTime;

@Service

public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager; // Используем AuthenticationManager

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder encoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public boolean login(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            return true;

        } catch (AuthenticationException e) {

            return false;
        }
    }

    @Override
    public boolean register(RegisterDto registerDto) {
        // Проверяем, существует ли пользователь
        if (userRepository.findByUsername(registerDto.getUsername()).isPresent() ) {
            return false;
        }

        // Создаем нового пользователя
        User user = new User();

        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setPhone(registerDto.getPhone());
        user.setPassword(encoder.encode(registerDto.getPassword()));
        user.setRole(Role.USER);
        user.setUsername(registerDto.getUsername());
        user.setEnabled(true);
        user.setRegistrationDate(LocalDateTime.now());

        userRepository.save(user);
        return true;
    }
}
