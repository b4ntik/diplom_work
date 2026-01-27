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
}
