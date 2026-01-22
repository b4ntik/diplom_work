package ru.skypro.homework.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exceptions.UserNotFoundException;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.utils.AdMapper;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdService {

    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final AdMapper adMapper;

    public AdService(AdRepository adRepository,
                     UserRepository userRepository,
                     AdMapper adMapper) {
        this.adRepository = adRepository;
        this.userRepository = userRepository;
        this.adMapper = adMapper;
    }

    @Transactional(readOnly = true)
    public AdsDto getAllAds() {
        List<Ad> ads = adRepository.findAll();
        AdsDto dto = new AdsDto();
        dto.setCount(ads.size());
        dto.setResults(ads.stream().map(adMapper::toDto).collect(Collectors.toList()));
        return dto;
    }

    @Transactional
    public AdDto createAd(CreateOrUpdateAdDto dto, MultipartFile image, String username) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ad ad = new Ad();
        ad.setTitle(dto.getTitle());
        ad.setDescription(dto.getDescription());
        ad.setPrice(dto.getPrice());
        if (image != null && !image.isEmpty()) {
            String imagePath = "/uploads/ads/" + image.getOriginalFilename();
            // здесь можно сохранить файл на диск
            ad.setImage(imagePath);
        }
        ad.setAuthor(author);
        Ad saved = adRepository.save(ad);
        return adMapper.toDto(saved);
    }
    public List<AdDto> getAdsByUser(String username) {
        log.debug("Поиск объявлений пользователя: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + username));

        List<Ad> userAds = adRepository.findByAuthor(user);

        return userAds.stream()
                .map(adMapper::toDto)
                .collect(Collectors.toList());
    }
}
