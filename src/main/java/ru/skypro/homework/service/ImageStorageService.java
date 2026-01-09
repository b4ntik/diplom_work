package ru.skypro.homework.service;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.UUID;

@Service
@Slf4j
public class ImageStorageService {

    @Value("${app.upload.path:uploads}")
    private String uploadPath;

    public String saveImage(MultipartFile imageFile) throws IOException {
        String originalFilename = imageFile.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID() + extension;

        Path filePath = Paths.get(uploadPath).resolve(filename);
        Files.createDirectories(filePath.getParent());
        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filename;
    }

    public String saveImageFromBase64(String imageBase64) throws IOException {
        if (imageBase64.contains(",")) {
            imageBase64 = imageBase64.split(",")[1];
        }

        byte[] imageBytes = Base64.getDecoder().decode(imageBase64);

        //определяем тип изображения
        String extension = determineImageExtension(imageBytes);
        String filename = UUID.randomUUID() + "." + extension;

        Path filePath = Paths.get(uploadPath).resolve(filename);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, imageBytes);

        return filename;
    }

    public void deleteImage(String filename) throws IOException {
        Path filePath = Paths.get(uploadPath).resolve(filename);
        Files.deleteIfExists(filePath);
    }

    private String determineImageExtension(byte[] imageBytes) {
        //проверка первых байтов
        if (imageBytes[0] == (byte) 0xFF && imageBytes[1] == (byte) 0xD8) {
            return "jpg";
        } else if (imageBytes[0] == (byte) 0x89 && imageBytes[1] == (byte) 0x50) {
            return "png";
        }
        return "jpg"; // По умолчанию
    }
}
