package ru.skypro.homework.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.service.AdService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ads")

public class AdController {

    private final AdService adService;
    private final ObjectMapper objectMapper;

    public AdController(AdService adService, ObjectMapper objectMapper) {
        this.adService = adService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<AdsDto> getAllAds() {
        return ResponseEntity.ok(adService.getAllAds());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdDto> addAd(
            @RequestParam("properties") String propertiesJson,
            @RequestParam("image") MultipartFile image,
            Principal principal) throws JsonProcessingException {

        // Парсим JSON в DTO
        CreateOrUpdateAdDto dto = objectMapper.readValue(propertiesJson, CreateOrUpdateAdDto.class);

        log.info("Загрузка объявления пользователем: {}", principal.getName());
        log.info("Размер файла: {} байт", image.getSize());
        log.info("Тип файла: {}", image.getContentType());
        log.info("Имя файла: {}", image.getOriginalFilename());

        AdDto ad = adService.createAd(dto, image, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(ad);
    }

    @GetMapping("/me")
    public ResponseEntity<List<AdDto>> getMyAds(Principal principal) {
        log.info("Получение объявлений пользователя: {}", principal.getName());

        List<AdDto> userAds = adService.getAdsByUser(principal.getName());
        log.info("Найдено объявлений: {}", userAds.size());

        return ResponseEntity.ok(userAds);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<AdDto> deleteAd(@PathVariable Long id, Principal principal) {
        log.info("удаление объявления ID: {}", id);
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            adService.deleteAd(id, principal.getName());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    };
    @PatchMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<?> updateImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile image,
            Principal principal,
            @RequestHeader(value = "Accept", defaultValue = MediaType.APPLICATION_JSON_VALUE) String accept) {

        if (principal == null) {
            log.error("Пользователь не аутентифицирован при обновлении изображения объявления ID: {}", id);
            return ResponseEntity.status(401).build();
        }
        try {
            AdDto updatedAd = adService.updateAdImage(id, image, principal.getName());

            // Определяем формат ответа на основе Accept заголовка
            MediaType mediaType = MediaType.parseMediaType(accept);

            if (mediaType.includes(MediaType.APPLICATION_OCTET_STREAM)) {
                // Возвращаем только изображение как бинарные данные
                Path imagePath = Paths.get(updatedAd.getImage().substring(1)); // убираем ведущий /
                byte[] imageBytes = Files.readAllBytes(imagePath);

                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header("Content-Disposition", "attachment; filename=\"" + imagePath.getFileName() + "\"")
                        .body(imageBytes);
            } else {
                // По умолчанию возвращаем JSON
                return ResponseEntity.ok(updatedAd);
            }
        } catch (RuntimeException | IOException e) {
            return ResponseEntity.status(404).build();
        }

    }
}
