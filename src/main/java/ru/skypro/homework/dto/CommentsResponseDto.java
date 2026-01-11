package ru.skypro.homework.dto;

import java.util.List;

public class CommentsResponseDto {
    private int count;
    private List<CommentResponseDto> results;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<CommentResponseDto> getResults() {
        return results;
    }

    public void setResults(List<CommentResponseDto> results) {
        this.results = results;
    }
}
