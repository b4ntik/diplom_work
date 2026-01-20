package ru.skypro.homework.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.dto.UpdateAdsDto;
import ru.skypro.homework.dto.UpdateImageResponse;
import ru.skypro.homework.exceptions.UserNotFoundException;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.ImageStorageService;
import ru.skypro.homework.utils.AdMapper;

import java.io.IOException;
import java.util.List;

@CrossOrigin(value = "http://localhost:3000")
@RestController
public class AdsController {

    private final AdService adsService;
    private final UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ImageStorageService imageStorageService;

    public AdsController(AdService adsService, UserRepository userRepository) {
        this.adsService = adsService;
        this.userRepository = userRepository;

    }

    //создание объявления
    @PostMapping(value ="/ads",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AdsDto> createAd(
            @RequestPart("properties") String propertiesJson,
            @RequestPart("image") MultipartFile image) throws IOException {

        // Парсим JSON строку в объект CreateAdsDto
        CreateAdsDto createAdsDto = objectMapper.readValue(propertiesJson, CreateAdsDto.class);

        // Создаем объявление
        AdsDto createdAd = adsService.createAd(createAdsDto, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAd);
    }
    //получить все объявления
    @GetMapping("/ads")
    public ResponseEntity<?> getAllAds(){
        List<AdsDto> allAds = adsService.getAllAds();
        return ResponseEntity.ok(allAds);
    }
    //получить информацию об объявлении
    @GetMapping("/ads/{id}")
    public ResponseEntity<?> getAdsInfo(@PathVariable Long id){
        AdsDto adsInfo = adsService.getAdsInfo(id);
        return ResponseEntity.ok(adsInfo);
    }
    //удалить объявление
    @DeleteMapping("/ads/{id}")
    public ResponseEntity<?> deleteAds(){
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    //изменить объявление
    @PatchMapping("/ads/{id}")
    public ResponseEntity<?> updateAds(@PathVariable Long id,
                                       @RequestBody UpdateAdsDto updateAdsDto,
                                       Authentication authentication) throws Exception {
        String username = authentication.getName();
        AdsDto updatedAd = adsService.updateAd(id, updateAdsDto, username);

        return ResponseEntity.ok(updatedAd);
    }
    //получить объявления пользователя
    @GetMapping("/ads/me")
    public ResponseEntity<?> getAdsByUser(Authentication authentication){
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = authentication.getName();

        try {
            List<AdsDto> adsByUser = adsService.getAdsByUsername(username);
            return ResponseEntity.ok(adsByUser);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    //изменить картинку
    @PatchMapping(value="/ads/{id}/image",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateAdsImage(@PathVariable Long id,
                                            @RequestParam("image") MultipartFile image,
                                            Authentication authentication) throws Exception {
        String email = authentication.getName();
        imageStorageService.saveAdImage(image);
        return ResponseEntity.ok().build();
    }
}
