package ru.skypro.homework.service;

import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.Role;

import java.nio.file.AccessDeniedException;

@Service
public class UserService {
    public User updateUserInfo(Long userId, UserUpdateDto updateDto, User currentUser) throws AccessDeniedException {
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

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
                throw new AccessDeniedException("Только амин может менять роль!");
            }
            user.setRole(updateDto.getRole());
        }
        if (updateDto.getUserImage() != null){
            userToUpdate.setUserImage(updateDto.getUserImage());
        }

        return userRepository.save(userToUpdate);
    }
}
