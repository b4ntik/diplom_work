package ru.skypro.homework.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.UpdateAdsDto;
import ru.skypro.homework.dto.UpdateImageResponse;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdService;

import java.util.List;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
public class AdsController {

    private final AdService adsService;
    private final UserRepository userRepository;

    //создание объявления
    @PostMapping(path = "/ads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createAds(
            @RequestPart("properties") @Valid AdsDto createAdsDto,
            @RequestPart(value = "image", required = true)MultipartFile imageFile,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            AdsDto createdAds = adsService.createAds(createAdsDto, imageFile, username);


            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(createdAds);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
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
        String username = authentication.getName();
        List<AdsDto> adsByUser = adsService.getAdsByUsername(username);
        return ResponseEntity.ok(adsByUser);
    }
    //изменить картинку
    @PatchMapping("/ads/{id}/image")
    public ResponseEntity<?> updateAdsImage(@PathVariable Long id,
                                            @RequestBody String image,
                                            Authentication authentication) throws Exception {
        String username = authentication.getName();
        UpdateImageResponse response = adsService.updateAdImage(id, image, username);
        return ResponseEntity.ok(response);
    }
}
