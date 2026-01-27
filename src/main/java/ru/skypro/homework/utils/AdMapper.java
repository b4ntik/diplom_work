package ru.skypro.homework.utils;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.User;

@Mapper(componentModel = "spring", uses = UserIdMapper.class)
public interface AdMapper {
    // Преобразование Ad -> AdDto:
    // автор (User) превращаем в id автора (Long) через именованный метод mapUserId
    @Mapping(source = "id", target = "pk")
    @Mapping(source = "author.id", target = "author")
    AdDto toDto(Ad ad);

    // Преобразование AdDto -> Ad:
    // id автора (Long) превращаем обратно в User через именованный метод mapUserFromId
    @Mapping(source = "pk", target = "id")
    @Mapping(source = "author", target = "author", qualifiedByName = "idToUser")
    Ad toEntity(AdDto dto);
    @Named("idToUser")
    default User idToUser(Long authorId) {
        if (authorId == null) {
            return null;
        }
        User user = new User();
        user.setId(authorId);
        return user;
    }
}
