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

    @Value("${app.file.upload-dir}")
    private String uploadDir;

    /**
     * Сохранить изображение объявления
     */
    public String saveAdImage(MultipartFile imageFile, String username) throws IOException {
        String originalFilename = imageFile.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // Создаем безопасное имя файла
        String safeUsername = username.replace("@", "_").replace(".", "_");
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().substring(0, 8);

        String filename = "ad_" + timestamp + "_" + safeUsername + "_" + uuid + extension;

        return saveImage(imageFile, filename, "ads");
    }

    /**
     * Сохранить изображение и вернуть корректный путь
     */
    private String saveImage(MultipartFile imageFile, String filename, String subdirectory) throws IOException {
        Path uploadPath = Paths.get(uploadDir, subdirectory);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(filename);
        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Всегда возвращаем путь в формате /uploads/subdirectory/filename
        return "/uploads/" + subdirectory + "/" + filename;
    }

    /**
     * Нормализовать путь к изображению (исправить некорректные пути)
     */
    public String normalizeImagePath(String imagePath) {
        if (imagePath == null || imagePath.isEmpty() || imagePath.equals("null")) {
            return "/images/default-ad.jpg";
        }

        // Удаляем возможные кавычки
        String normalized = imagePath.trim();
        if (normalized.startsWith("\"") && normalized.endsWith("\"")) {
            normalized = normalized.substring(1, normalized.length() - 1);
        }

        // Удаляем /./ в начале если есть
        if (normalized.startsWith("/./")) {
            normalized = normalized.substring(2);
        }

        // Если путь уже начинается с /uploads/, возвращаем как есть
        if (normalized.startsWith("/uploads/")) {
            return normalized;
        }

        // Если начинается с uploads/ без слеша, добавляем
        if (normalized.startsWith("uploads/")) {
            return "/" + normalized;
        }

        // Если это путь без uploads/, добавляем префикс
        if (!normalized.contains("uploads/")) {
            return "/uploads/ads/" + normalized;
        }

        // Во всех остальных случаях добавляем начальный слеш
        return normalized.startsWith("/") ? normalized : "/" + normalized;
    }

    /**
     * Проверить, существует ли изображение
     */
    public boolean imageExists(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return false;
        }

        String normalizedPath = normalizeImagePath(imagePath);
        String relativePath = normalizedPath.startsWith("/") ?
                normalizedPath.substring(1) : normalizedPath;

        Path filePath = Paths.get(uploadDir, relativePath.replaceFirst("^uploads/", ""));
        return Files.exists(filePath);
    }
}