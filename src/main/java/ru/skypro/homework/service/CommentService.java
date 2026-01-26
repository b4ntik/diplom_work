package ru.skypro.homework.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.entity.*;
import ru.skypro.homework.entity.Role;
import ru.skypro.homework.exceptions.*;
import ru.skypro.homework.repository.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final AdRepository adRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          AdRepository adRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.adRepository = adRepository;
        this.userRepository = userRepository;
    }

    // Получение всех комментариев объявления
    public CommentsDto getComments(Integer adId) {
        Long adIdLong = adId.longValue();

        // Проверяем существование объявления
        Ad ad = adRepository.findById(adIdLong)
                .orElseThrow(() -> new AdNotFoundException("Объявление не найдено"));

        List<Comment> comments = commentRepository.findByAdIdOrderByCreatedAtDesc(adIdLong);

        CommentsDto response = new CommentsDto();
        response.setCount(comments.size());
        response.setResults(comments.stream()
                .map(this::convertToCommentDto)
                .collect(Collectors.toList()));

        return response;
    }

    // Добавление комментария
    public CommentDto addComment(Integer adId, CreateOrUpdateCommentDto createOrUpdateCommentDto, String username) {
        Long adIdLong = adId.longValue();

        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        Ad ad = adRepository.findById(adIdLong)
                .orElseThrow(() -> new AdNotFoundException("Объявление не найдено"));

        Comment comment = new Comment();
        comment.setText(createOrUpdateCommentDto.getText());
        comment.setAuthor(author);
        comment.setAd(ad);
        comment.setCreatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);

        return convertToCommentDto(savedComment);
    }

    // Удаление комментария
    public void deleteComment(Integer adId, Integer commentId, String username) {
        Long adIdLong = adId.longValue();
        Long commentIdLong = commentId.longValue();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        // Проверяем существование объявления
        adRepository.findById(adIdLong)
                .orElseThrow(() -> new AdNotFoundException("Объявление не найдено"));

        Comment comment = commentRepository.findById(commentIdLong)
                .orElseThrow(() -> new CommentNotFoundException("Комментарий не найден"));

        // Проверяем, что комментарий принадлежит указанному объявлению
        if (!comment.getAd().getId().equals(adIdLong)) {
            throw new CommentNotFoundException("Комментарий не принадлежит указанному объявлению");
        }

        // Проверка прав доступа
        boolean isAuthor = comment.getAuthor().getId().equals(currentUser.getId());
        boolean isAdAuthor = comment.getAd().getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().equals(Role.ADMIN);

        if (!isAuthor && !isAdAuthor && !isAdmin) {
            throw new AccessDeniedException("Нет прав для удаления комментария");
        }

        commentRepository.delete(comment);
    }

    // Обновление комментария
    public CommentDto updateComment(Integer adId, Integer commentId,
                                    CreateOrUpdateCommentDto updateRequest,
                                    String username) {
        Long adIdLong = adId.longValue();
        Long commentIdLong = commentId.longValue();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        // Проверяем существование объявления
        adRepository.findById(adIdLong)
                .orElseThrow(() -> new AdNotFoundException("Объявление не найдено"));

        Comment comment = commentRepository.findById(commentIdLong)
                .orElseThrow(() -> new CommentNotFoundException("Комментарий не найден"));

        // Проверяем, что комментарий принадлежит указанному объявлению
        if (!comment.getAd().getId().equals(adIdLong)) {
            throw new CommentNotFoundException("Комментарий не принадлежит указанному объявлению");
        }

        // Проверка прав доступа
        boolean isAuthor = comment.getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().equals(Role.ADMIN);

        if (!isAuthor && !isAdmin) {
            throw new AccessDeniedException("Нет прав для редактирования комментария");
        }

        // Обновляем текст комментария
        comment.setText(updateRequest.getText());
        comment.setUpdatedAt(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(comment);

        return convertToCommentDto(updatedComment);
    }

    // Вспомогательный метод для конвертации в CommentDto
    private CommentDto convertToCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();

        // ID комментария
        if (comment.getId() != null) {
            dto.setPk(comment.getId().intValue());
        }

        // Автор комментария
        if (comment.getAuthor() != null) {
            if (comment.getAuthor().getId() != null) {
                dto.setAuthor(comment.getAuthor().getId().intValue());
            }
            dto.setAuthorFirstName(comment.getAuthor().getFirstName());
            dto.setAuthorImage(comment.getAuthor().getImage());
        }

        dto.setText(comment.getText());

        // Преобразование LocalDateTime в миллисекунды
        if (comment.getCreatedAt() != null) {
            long millis = comment.getCreatedAt()
                    .atZone(ZoneOffset.systemDefault())
                    .toInstant()
                    .toEpochMilli();
            dto.setCreatedAt(millis);
        }

        return dto;
    }
}