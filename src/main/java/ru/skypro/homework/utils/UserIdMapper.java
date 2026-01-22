package ru.skypro.homework.utils;

import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.repository.UserRepository;

@Component
public class UserIdMapper {

    @Autowired
    private UserRepository userRepository;

    // User -> Long
    @Named("toId")
    public Long map(User user) {
        return user != null ? user.getId() : null;
    }

    // Long -> User (если нужен обратный маппинг; загрузка через репозиторий)
    @Named("toUser")
    public User map(Long id) {
        if (id == null) {
            return null;
        }
        return userRepository.findById(id).orElse(null);
    }
}