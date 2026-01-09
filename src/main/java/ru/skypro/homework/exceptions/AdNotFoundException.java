package ru.skypro.homework.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AdNotFoundException extends RuntimeException {
    private Long adId;

    public AdNotFoundException(String message) {
        super(message);
    }
    public AdNotFoundException(Long adId) {
        super(String.format("Объявление с id %d не найдено", adId));
        this.adId = adId;
    }
}
