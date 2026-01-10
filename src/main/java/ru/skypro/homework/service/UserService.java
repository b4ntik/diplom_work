package ru.skypro.homework.service;

import org.springframework.data.crossstore.ChangeSetPersister;

import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.exceptions.UserNotFoundException;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.utils.UserMapper;

import java.nio.file.AccessDeniedException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
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
        if (updateDto.getUserPhone() != null){
            userToUpdate.setUserPhone(updateDto.getUserPhone());
        }
        if (updateDto.getRole() != null && !updateDto.getRole().equals(userToUpdate.getRole())){
            if(!currentUser.getRole().equals(Role.ADMIN)){
                throw new AccessDeniedException("Только админ может менять роль!");
            }
            currentUser.setRole(updateDto.getRole());
        }
        if (updateDto.getUserImage() != null){
            userToUpdate.setUserImage(updateDto.getUserImage());
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

}
