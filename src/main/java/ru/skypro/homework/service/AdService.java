package ru.skypro.homework.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ExtendedAdDto;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exceptions.UserNotFoundException;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.utils.AdMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdService {



    @Value("${app.upload.dir:uploads}")  // Если свойство не найдено, будет "uploads"
    private String uploadDir;

    @Value("${app.file.ad-images-dir}")
    private String adImagesDir;


    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final AdMapper adMapper;
    @Autowired
    private ImageStorageService imageStorageService;

    public AdService(AdRepository adRepository,
                     UserRepository userRepository,
                     AdMapper adMapper, ImageStorageService imageStorageService) {
        this.adRepository = adRepository;
        this.userRepository = userRepository;
        this.adMapper = adMapper;
        this.imageStorageService = imageStorageService;
    }

    @Transactional(readOnly = true)
    public AdsDto getAllAds() {
        List<Ad> ads = adRepository.findAllByOrderByCreatedAtDesc();
        AdsDto dto = new AdsDto();
        dto.setCount(ads.size());

        // Преобразуем Ads в AdDto с нормализацией путей к изображениям
        List<AdDto> normalizedAds = ads.stream()
                .map(ad -> {
                    AdDto adDto = adMapper.toDto(ad);

                    // Нормализуем путь к изображению
                    if (adDto.getImage() != null) {
                        String normalizedPath = imageStorageService.normalizeImagePath(adDto.getImage());
                        adDto.setImage(normalizedPath);
                    }

                    return adDto;
                })
                .collect(Collectors.toList());

        dto.setResults(normalizedAds);
        return dto;
    }

    @Transactional
    public AdDto createAd(CreateOrUpdateAdDto dto, MultipartFile image, String username) {
        log.info("Создание объявления: title={}, price={}, user={}",
                dto.getTitle(), dto.getPrice(), username);

        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ad ad = new Ad();
        ad.setTitle(dto.getTitle());
        ad.setDescription(dto.getDescription());
        ad.setPrice(dto.getPrice());
        ad.setAuthor(author);

        try {
            // 1. Создаем уникальное имя файла
            String originalFilename = image.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String fileName = generateFileName(originalFilename, username);

            // 2. Подготовка пути
            Path uploadPath = Paths.get(adImagesDir).toAbsolutePath().normalize();

            // 3. Создаем директории если не существуют
            Files.createDirectories(uploadPath);
            log.info("Директория для загрузок: {}", uploadPath.toString());

            // 4. Сохраняем файл
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Файл сохранен: {}", filePath.toString());

            // 5. Сохраняем путь в базе (с ведущим / для URL)
            String imagePath = imageStorageService.saveAdImage(image, username);
            ad.setImage(imagePath);

        } catch (IOException e) {
            log.error("Ошибка сохранения файла: {}", e.getMessage());
            throw new RuntimeException("Не удалось сохранить изображение: " + e.getMessage());
        }
        Ad saved = adRepository.save(ad);
        return adMapper.toDto(saved);
    }

    public AdsDto getAdsByUsername(String username) {
        log.debug("Поиск объявлений пользователя: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + username));

        List<Ad> userAds = adRepository.findByAuthor(user);

        // Преобразуем Ads в AdDto с нормализацией путей к изображениям
        List<AdDto> results = userAds.stream()
                .map(ad -> {
                    AdDto adDto = adMapper.toDto(ad);

                    // Нормализуем путь к изображению
                    if (adDto.getImage() != null) {
                        String normalizedPath = imageStorageService.normalizeImagePath(ad.getImage());
                        adDto.setImage(normalizedPath);
                    } else {
                        adDto.setImage("/images/default-ad.jpg");
                    }

                    return adDto;
                })
                .collect(Collectors.toList());

        // Создаем ответ в нужном формате
        AdsDto response = new AdsDto();
        response.setCount(results.size());
        response.setResults(results);

        return response;
    }

    public ExtendedAdDto getAdById(Long id) {
        log.debug("Поиск объявления по ID: {}", id);

        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Объявление не найдено с ID: {}", id);
                    return new RuntimeException("Ad not found with id: " + id);
                });

        log.debug("Объявление найдено: ID={}, title={}", ad.getId(), ad.getTitle());

        // Преобразуем в ExtendedAdDto
        ExtendedAdDto extendedDto = adMapper.toExtendedDto(ad);

        // Нормализуем путь к изображению
        if (extendedDto.getImage() != null) {
            String normalizedImage = imageStorageService.normalizeImagePath(extendedDto.getImage());
            extendedDto.setImage(normalizedImage);
        } else {
            extendedDto.setImage("/images/default-ad.jpg");
        }

        // Если email не указан в объявлении, берем из профиля автора
        if ((extendedDto.getEmail() == null || extendedDto.getEmail().isEmpty())
                && ad.getAuthor() != null) {
            extendedDto.setEmail(ad.getAuthor().getUsername());
        }

        // Если телефон не указан в объявлении, берем из профиля автора
        if ((extendedDto.getPhone() == null || extendedDto.getPhone().isEmpty())
                && ad.getAuthor() != null) {
            extendedDto.setPhone(ad.getAuthor().getPhone());
        }

        return extendedDto;
    }

    public void deleteAd(Long adId, String username) {
        log.debug("Удаление объявления ID: {} пользователем: {}", adId, username);

        // Находим пользователя
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + username));

        // Находим объявление
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new RuntimeException("Объявление не найдено с ID: " + adId));

        // Проверяем, что пользователь является автором объявления
        if (!ad.getAuthor().getId().equals(user.getId())) {
            log.warn("Попытка удаления чужого объявления. Пользователь: {}, автор объявления: {}",
                    username, ad.getAuthor().getUsername());
            throw new RuntimeException("Вы не можете удалить чужое объявление");
        }

        // Удаляем связанные файлы (если есть)
        deleteAdImage(ad.getImage());

        // Удаляем объявление из базы
        adRepository.delete(ad);

        log.debug("Объявление ID: {} удалено", adId);
    }

    private void deleteAdImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                java.nio.file.Path filePath = java.nio.file.Paths.get(imagePath);
                java.nio.file.Files.deleteIfExists(filePath);
                log.debug("Файл изображения удален: {}", imagePath);
            } catch (Exception e) {
                log.warn("Не удалось удалить файл изображения {}: {}", imagePath, e.getMessage());
            }
        }
    }
    public AdDto updateAdImage(Long adId, MultipartFile image, String username) {
        log.debug("Обновление изображения для объявления ID: {}, пользователь: {}", adId, username);

        // Находим пользователя
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + username));

        // Находим объявление
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new RuntimeException("Объявление не найдено с ID: " + adId));

        // Проверяем права доступа
        if (!ad.getAuthor().getId().equals(user.getId())) {
            log.warn("Попытка обновления чужого объявления. Пользователь: {}, автор: {}",
                    username, ad.getAuthor().getUsername());
            throw new RuntimeException("Вы не можете обновлять чужое объявление");
        }

        // Удаляем старое изображение если оно существует
        deleteAdImage(ad.getImage());

        // Сохраняем новое изображение
        String newImagePath = saveImage(image, adId, username);

        // Обновляем объявление
        ad.setImage(newImagePath);
        Ad savedAd = adRepository.save(ad);

        log.info("Изображение обновлено для объявления ID: {}, новый путь: {}", adId, newImagePath);

        return adMapper.toDto(savedAd);
    }
    private String saveImage(MultipartFile image, Long adId, String username) {
        try {
            // Создаем директорию если не существует
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.debug("Создана директория для загрузок: {}", uploadDir);
            }

            // Генерируем уникальное имя файла
            String originalFilename = image.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String fileName = String.format("ad_%d_%s_%s%s",
                    adId,
                    username.replaceAll("[^a-zA-Z0-9]", "_"),
                    UUID.randomUUID().toString().substring(0, 8),
                    fileExtension);

            // Сохраняем файл
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String imagePath = "/" + uploadDir + fileName;
            log.debug("Файл сохранен: {}", imagePath);

            return imagePath;

        } catch (IOException e) {
            log.error("Ошибка сохранения файла: {}", e.getMessage());
            throw new RuntimeException("Не удалось сохранить файл: " + e.getMessage());
        }
    }
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".jpg"; // значение по умолчанию
        }
        return filename.substring(filename.lastIndexOf("."));
    }


    private String generateFileName(String originalFilename, String username) {
        // Создаем уникальное имя: timestamp_username_uuid.ext
        String safeUsername = username.replaceAll("[^a-zA-Z0-9]", "_");
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String timestamp = String.valueOf(System.currentTimeMillis());

        String baseName = "ad_" + timestamp + "_" + safeUsername + "_" + uuid;
        String extension = getFileExtension(originalFilename);

        return baseName + extension;
    }


}
