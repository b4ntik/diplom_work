package ru.skypro.homework.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service

public class ImageStorageService {

    @Value("${app.file.upload-dir:./uploads}")
    private String uploadDir;

    @Value("${app.file.user-images-dir:${app.file.upload-dir}/users}")
    private String userImagesDir;

    @Value("${app.file.ad-images-dir:${app.file.upload-dir}/ads}")
    private String adImagesDir;

    /**
     * Сохранить изображение
     */
    public String saveImage(MultipartFile imageFile, String filename, String subdirectory) throws IOException {
        // Если uploadDir не настроен, используем временную директорию
        String baseDir = uploadDir != null ? uploadDir : "./uploads";
        Path uploadPath = Paths.get(baseDir, subdirectory);

        // Создаем директорию если не существует
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Сохраняем файл
        Path filePath = uploadPath.resolve(filename);
        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Возвращаем относительный путь
        return "/uploads/" + subdirectory + "/" + filename;
    }

    /**
     * Сохранить изображение пользователя
     */
    public String saveUserImage(MultipartFile imageFile) throws IOException {
        String originalFilename = imageFile.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String filename = "user_" + UUID.randomUUID() + extension;
        return saveImage(imageFile, filename, "users");
    }

    /**
     * Сохранить изображение объявления
     */
    public String saveAdImage(MultipartFile imageFile) throws IOException {
        String originalFilename = imageFile.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String filename = "ad_" + UUID.randomUUID() + extension;
        return saveImage(imageFile, filename, "ads");
    }

    /**
     * Удалить изображение
     */
    public void deleteImage(String imagePath) throws IOException {
        if (imagePath == null || imagePath.isEmpty()) {
            return;
        }

        try {
            // Извлекаем относительный путь из URL
            String relativePath = imagePath.replaceFirst("/uploads/", "");
            Path fullPath = Paths.get(uploadDir, relativePath);

            if (Files.exists(fullPath)) {
                Files.delete(fullPath);
            }
        } catch (Exception e) {
            // Логируем ошибку, но не выбрасываем исключение
            System.err.println("Не удалось удалить изображение: " + e.getMessage());
        }
    }

    /**
     * Получить изображение как массив байт
     */
    public byte[] getImage(String imagePath) throws IOException {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }

        String relativePath = imagePath.replaceFirst("/uploads/", "");
        Path fullPath = Paths.get(uploadDir, relativePath);

        if (Files.exists(fullPath)) {
            return Files.readAllBytes(fullPath);
        }

        return null;
    }
    public String getImagePath(String imageUrl){
        try {
            // Удаляем начальный слэш если есть
            String cleanUrl = imageUrl.startsWith("/") ? imageUrl.substring(1) : imageUrl;

            // Преобразуем URL в путь на файловой системе
            Path path = Paths.get(uploadDir, cleanUrl.replaceFirst("^uploads/", ""));

            // Возвращаем абсолютный путь в виде строки
            return path.toAbsolutePath().toString();

        } catch (Exception e) {
            System.err.println("Ошибка при получении пути к изображению: " + e.getMessage());
            return null;
        }
    }
}