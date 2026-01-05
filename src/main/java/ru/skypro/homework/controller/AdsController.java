package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.AdsDto;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
public class AdsController {

    private final AdsService adsService;

    //создание объявления
    @PostMapping("/ads")
    public ResponseEntity<?> createAds(@RequestBody ){
    return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    //получить все объявления
    @GetMapping("/ads")
    public ResponseEntity<?> getAllAds(){
        return ResponseEntity.ok(allAds);
    }
    //получить информацию об объявлении
    @GetMapping("/ads/{id}")
    public ResponseEntity<?> getAdsInfo(){
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
                                       @RequestBody AdsDto updateAdsDto,
                                       Authentication authentication){
        String username = authentication.getName();
        AdsDto updatedAd = adsService.updateAd(id, updateAdsDto, username);

        return ResponseEntity.ok(updatedAd);
    }
    //получить объявления пользователя
    @GetMapping("/ads/me")
    public ResponseEntity<?> getAdsByUser(Authentication authentication){
        String username = authentication.getName();
        return ResponseEntity.ok(adsByUser);
    }
    //изменить картинку
    @PatchMapping("/ads/{id}/image")
    public ResponseEntity<?> updateAdsImage(@PathVariable Long id,
                                            @RequestBody String image,
                                            Authentication authentication){
        String username = authentication.getName();
        UpdateImageResponse response = adService.updateAdImage(id, image, username);
        return ResponseEntity.ok(response);
    }
}
