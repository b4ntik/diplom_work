package ru.skypro.homework.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdService;

import java.time.LocalDateTime;

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
            @RequestPart("properties") @Valid AdsDto createAdRequestDto,
            @RequestPart(value = "image", required = true)MultipartFile imageFile,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            AdsDto createdAds = adsService.createdAds(createdAdsDto, imageFile, username);


            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(createdAds);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.warn("Ошибка валидации: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
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
        UpdateImageResponse response = adsService.updateAdImage(id, image, username);
        return ResponseEntity.ok(response);
    }
}
