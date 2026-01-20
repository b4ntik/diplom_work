package ru.skypro.homework.utils;


import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.entity.Ad;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import org.springframework.context.annotation.

@Component
public class AdMapper {

        public AdsDto toDto(Ad ad) {
            if (ad == null) {
                return null;
            }

            AdsDto dto = new AdsDto();
            dto.setId(ad.getId());
            dto.setTitle(ad.getTitle());
            dto.setDescription(ad.getDescription());
            dto.setPrice(ad.getPrice());

            // Изображение
            if (ad.getImagePath() != null && !ad.getImagePath().isEmpty()) {
                dto.setImageUrl("/images/" + ad.getImagePath());
            } else {
                dto.setImageUrl("/images/default-ad.jpg");
            }

            // Автор
            if (ad.getAuthor() != null) {
                dto.setAuthorId(ad.getAuthor().getId());
                dto.setAuthorName(ad.getAuthor().getUsername());
            }

            dto.setCreatedAt(ad.getCreatedAt());
            //dto.setViews(ad.getViews() != null ? ad.getViews() : 0);

            return dto;
        }

        public List<AdsDto> toDtoList(List<Ad> ads) {
            if (ads == null) {
                return new ArrayList<>();
            }

            List<AdsDto> result = new ArrayList<>();
            for (Ad ad : ads) {
                result.add(toDto(ad));
            }
            return result;
        }
}