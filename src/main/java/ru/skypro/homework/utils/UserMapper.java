package ru.skypro.homework.utils;


import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.dto.UserResponseDto;
import ru.skypro.homework.dto.UserUpdateDto;


@Component
public class UserMapper {

    public UserResponseDto toUserResponseDto(User user) {
        if (user == null) return null;

        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        dto.setImage(user.getImage() != null ? user.getImage() : "/images/default-avatar.jpg");
        dto.setRole(user.getRole() != null ? user.getRole().name() : "USER");

        return dto;
    }

    public void updateUserFromDto(UserUpdateDto updateDto, User user) {
    }
//    private final ModelMapper modelMapper;
//
//    public UserMapper() {
//        this.modelMapper = new ModelMapper();
//
//        // Базовая настройка
//        modelMapper.getConfiguration()
//                .setSkipNullEnabled(true)
//                .setFieldMatchingEnabled(true);
//    }
//
//    public UserResponseDto toUserResponseDto(User user) {
//        if (user == null) {
//            return null;
//        }
//
//        UserResponseDto dto = modelMapper.map(user, UserResponseDto.class);
//
//        // Дополнительные преобразования
//        if (user.getRole() != null) {
//            dto.setRole(user.getRole().name());
//        }
//
//        return dto;
//    }
//
//    public void updateUserFromDto(Object dto, User user) {
//        if (dto == null || user == null) {
//            return;
//        }
//
//        // Создаем временный ModelMapper с игнорированием полей
//        ModelMapper tempMapper = new ModelMapper();
//        tempMapper.getConfiguration().setSkipNullEnabled(true);
//
//        // Маппим, но игнорируем защищенные поля
//        tempMapper.typeMap(dto.getClass(), User.class)
//                .addMappings(mapper -> {
//                    mapper.skip(User::setPassword);
//                    mapper.skip(User::setRole);
//                    mapper.skip(User::setEnabled);
//                    mapper.skip(User::setRegistrationDate);
//                    mapper.skip(User::setAds);
//                    mapper.skip(User::setComments);
//                });
//
//        tempMapper.map(dto, user);
//    }
}