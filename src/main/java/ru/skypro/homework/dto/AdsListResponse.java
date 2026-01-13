package ru.skypro.homework.dto;


import java.util.List;

public class AdsListResponse {
    private Integer count;
    private List<AdsDto> results;

    public AdsListResponse(int size, List<AdsDto> adsDtos) {
    }
}