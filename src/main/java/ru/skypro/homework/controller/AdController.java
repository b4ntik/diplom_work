package ru.skypro.homework.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.service.AdService;

import java.security.Principal;

@RestController
@RequestMapping("/ads")
public class AdController {

    private final AdService adService;

    public AdController(AdService adService) {
        this.adService = adService;
    }

    @GetMapping
    public ResponseEntity<AdsDto> getAllAds() {
        return ResponseEntity.ok(adService.getAllAds());
    }

    @PostMapping
    public ResponseEntity<AdDto> addAd(@RequestBody CreateOrUpdateAdDto dto,
                                       @RequestParam("image") MultipartFile image,
                                       Principal principal) {
        AdDto ad = adService.createAd(dto, image, principal.getName());
        return new ResponseEntity<>(ad, HttpStatus.CREATED);
    }
}
