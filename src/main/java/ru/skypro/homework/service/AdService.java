package ru.skypro.homework.service;

import jakarta.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.exceptions.AccessDeniedException;
import ru.skypro.homework.exceptions.AdNotFoundException;
import ru.skypro.homework.exceptions.ImageProcessingException;
import ru.skypro.homework.exceptions.UserNotFoundException;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.utils.AdMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service

public class AdService {
    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final ImageStorageService imageStorageService;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/jpg",
            "image/webp",
            "image/gif"
    );
    private final AdMapper adMapper;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB максимальный размер

    public AdService(AdRepository adRepository, UserRepository userRepository, ImageStorageService imageStorageService, AdMapper adMapper) {
        this.adRepository = adRepository;
        this.userRepository = userRepository;
        this.imageStorageService = imageStorageService;
        this.adMapper = adMapper;
    }

    //создание объявления
    public AdsDto createAds(@Valid AdsDto createAdsDto, MultipartFile imageFile, String username) throws Exception {

        //получить пользователя
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        //валидация картинки
        validateImageFile(imageFile);

        // создание картинки
        Ad ad = new Ad();
        ad.setTitle(createAdsDto.getTitle());
        ad.setDescription(createAdsDto.getDescription());
        ad.setPrice(createAdsDto.getPrice());
        ad.setAuthor(author);
        ad.setPhone(createAdsDto.getPhone());
        ad.setEmail(createAdsDto.getEmail());
        ad.setCreatedAt(LocalDateTime.now());
        ad.setUpdatedAt(LocalDateTime.now());

        // сохранить изображение после валидации
        try {
            String imagePath = imageStorageService.saveImage(imageFile);
            ad.setImagePath(imagePath);
        } catch (IOException e) {
            throw new ImageProcessingException("Ошибка при сохранении изображения", e);
        }

        //сохранить в БД
        Ad savedAd = adRepository.save(ad);

        //конвертация в дто
        return convertToDto(savedAd);
    }
    //получить все объявления
    public List<AdsDto> getAllAds() {
        List<Ad> ads = adRepository.findAllByOrderByCreatedAtDesc();
        List<AdsDto> adsDtos = ads.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return adsDtos;
    }
    //получить информацию об объявлении
    public AdsDto getAdsInfo(Long id) {
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException(id));

        return convertToDto(ad);
    }
    //удалить объявление
    public void deleteAds(Long id, String username) throws Exception {
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException(id));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        // Проверяем права доступа
        if (!ad.getAuthor().getId().equals(user.getId()) &&
                !user.getRole().equals(Role.ADMIN)) {
            throw new Exception("Нет прав для удаления этого объявления");
        }
        //удалить изображение
        if (ad.getImagePath() != null) {
            try {
                imageStorageService.deleteImage(ad.getImagePath());
            } catch (IOException e) {
                //log.warn("Не удалось удалить изображение: {}", ad.getImagePath());
            }
        }

        adRepository.delete(ad);
    }
    //изменить объявление
    public AdsDto updateAd(Long id, UpdateAdsDto updateAdsDto, String username) throws Exception {
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException(id));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        //проверка права доступа
        if (!ad.getAuthor().getId().equals(user.getId()) &&
                !user.getRole().equals(Role.ADMIN)) {
            throw new Exception("Нет прав для редактирования этого объявления");
        }

        //частичное обновление - только по переданным значениям
        if (updateAdsDto.getTitle() != null) {
            ad.setTitle(updateAdsDto.getTitle());
        }
        if (updateAdsDto.getDescription() != null) {
            ad.setDescription(updateAdsDto.getDescription());
        }
        if (updateAdsDto.getPrice() != null) {
            ad.setPrice(updateAdsDto.getPrice());
        }
        if (updateAdsDto.getPhone() != null) {
            ad.setPhone(updateAdsDto.getPhone());
        }
        if (updateAdsDto.getEmail() != null) {
            ad.setEmail(updateAdsDto.getEmail());
        }

        ad.setUpdatedAt(LocalDateTime.now());
        Ad updatedAd = adRepository.save(ad);

        return convertToDto(updatedAd);
    }
    //получить объявления пользователя
    public AdsListResponse getAdsByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        List<Ad> userAds = adRepository.findByAuthorOrderByCreatedAtDesc(user);
        List<AdsDto> adsDtos = userAds.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new AdsListResponse(adsDtos.size(), adsDtos);
    }
    //изменить картинку
    public UpdateImageResponse updateAdImage(Long id, String imageBase64, String username) throws Exception {
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException(id));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        // проверка права доступа
        if (!ad.getAuthor().getId().equals(user.getId()) &&
                !user.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("Нет прав для изменения изображения");
        }

        //декодируем Base64 и сохраняем изображение
        String newImagePath;
        try {
            // Удаляем старое изображение
            if (ad.getImagePath() != null) {
                imageStorageService.deleteImage(ad.getImagePath());
            }

            // сохраняем новое
            newImagePath = imageStorageService.saveImageFromBase64(imageBase64);
            ad.setImagePath(newImagePath);
            ad.setUpdatedAt(LocalDateTime.now());
            adRepository.save(ad);

        } catch (IOException e) {
            throw new Exception("Ошибка при обновлении изображения", e);
        }

        //возвращаем URL нового изображения
        String imageUrl = "/images/" + newImagePath;
        return new UpdateImageResponse(imageUrl);
    }
    private AdsDto convertToDto(Ad ad) {
        AdsDto dto = new AdsDto();
        dto.setId(ad.getId());
        dto.setTitle(ad.getTitle());
        dto.setDescription(ad.getDescription());
        dto.setPrice(ad.getPrice());
        dto.setAuthorUsername(ad.getAuthor().getUsername());
        dto.setAuthorFirstName(ad.getAuthor().getFirstName());
        dto.setAuthorLastName(ad.getAuthor().getLastName());
        dto.setPhone(ad.getPhone());
        dto.setEmail(ad.getEmail());
        dto.setImageUrl(ad.getImagePath() != null ? "/images/" + ad.getImagePath() : null);
        dto.setCreatedAt(ad.getCreatedAt());
        return dto;
    }
    //вспомогательный метод для валидации
    private void validateImageFile(MultipartFile imageFile) throws Exception {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new Exception("Изображение обязательно для создания объявления");
        }

        //проверка размера
        validateFileSize(imageFile);

        //типа файла
        validateFileType(imageFile);

        //имени файла
        validateFileName(imageFile);
    }
    //вспомогательный метод для валидации размера картинки
    private void validateFileSize(MultipartFile imageFile) throws Exception {
        if (imageFile.getSize() > MAX_FILE_SIZE) {
            throw new Exception(
                    String.format("Размер файла не должен превышать %d MB", MAX_FILE_SIZE / (1024 * 1024))
            );
        }
    }
    //вспомогательный метод для валидации типа
    private void validateFileType(MultipartFile imageFile) throws Exception {
        String contentType = imageFile.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new Exception(
                    String.format("Неподдерживаемый формат изображения. Разрешены: %s",
                            String.join(", ", ALLOWED_CONTENT_TYPES))
            );
        }
    }
    //вспомогательный метод для валидации имени картинки
    private void validateFileName(MultipartFile imageFile) throws Exception {
        String originalFilename = imageFile.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains("..")) {
            throw new Exception("Недопустимое имя файла");
        }
    }
    public List<AdsDto> getAdsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        "Пользователь " + username + " не найден"
                ));

        // Получаем объявления пользователя
        List<Ad> ads = adRepository.findByAuthorOrderByCreatedAtDesc(user);

        // Преобразуем в DTO
        List<AdsDto> adsDtos = ads.stream()
                .map(adMapper::toDto)
                .collect(Collectors.toList());

        // Возвращаем DTO
        return adsDtos;
    }
}

