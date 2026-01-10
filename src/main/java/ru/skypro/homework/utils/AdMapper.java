package ru.skypro.homework.utils;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.entity.Ad;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdMapper {

    private final ModelMapper modelMapper;

    public AdMapper() {
        this.modelMapper = new ModelMapper();

        // Простая настройка - ModelMapper сам попробует смапить поля с одинаковыми именами
        modelMapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setFieldMatchingEnabled(true);
    }

    public AdsDto toDto(Ad ad) {
        if (ad == null) {
            return null;
        }

        // ModelMapper автоматически смапит простые поля с одинаковыми именами
        AdsDto dto = modelMapper.map(ad, AdsDto.class);

        // Ручной маппинг для сложных полей
        dto.setImageUrl(convertImagePath(ad.getImagePath()));

        if (ad.getAuthor() != null) {
            dto.setAuthorId(ad.getAuthor().getId());
            dto.setAuthorName(ad.getAuthor().getUsername());
                    }
        return dto;
    }

    private String convertImagePath(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return "/images/default-ad.jpg";
        }
        return "/images/" + imagePath;
    }

    public List<AdsDto> toDtoList(List<Ad> ads) {
        if (ads == null || ads.isEmpty()) {
            return List.of();
        }

        return ads.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}