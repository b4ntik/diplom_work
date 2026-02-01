package ru.skypro.homework.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class FileUploadDirectoriesInitializer {

    @Value("${app.file.upload-dir}")
    private String uploadDir;

    @Value("${app.file.user-images-dir}")
    private String userImagesDir;

    @Value("${app.file.ad-images-dir}")
    private String adImagesDir;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        try {
            // Создаем все необходимые директории
            Files.createDirectories(Paths.get(uploadDir));
            Files.createDirectories(Paths.get(userImagesDir));
            Files.createDirectories(Paths.get(adImagesDir));

            System.out.println("Директории для загрузок созданы:");
            System.out.println("Основная: " + Paths.get(uploadDir).toAbsolutePath());
            System.out.println("Пользователи: " + Paths.get(userImagesDir).toAbsolutePath());
            System.out.println("Объявления: " + Paths.get(adImagesDir).toAbsolutePath());

        } catch (Exception e) {
            System.err.println("Ошибка при создании директорий: " + e.getMessage());
        }
    }
}
