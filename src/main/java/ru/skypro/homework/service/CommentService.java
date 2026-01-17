package ru.skypro.homework.service;


import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.exceptions.AdNotFoundException;
import ru.skypro.homework.exceptions.UserNotFoundException;
import ru.skypro.homework.exceptions.CommentNotFoundException;
import ru.skypro.homework.exceptions.AccessDeniedException;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.utils.CommentMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class CommentService {

    private final CommentRepository commentRepository;
    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    public CommentService(CommentRepository commentRepository, AdRepository adRepository, UserRepository userRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.adRepository = adRepository;
        this.userRepository = userRepository;
        this.commentMapper = commentMapper;
    }

    //новый коммент
    public CommentResponseDto createComment(Long adId, CreateCommentDto createCommentDto, String username) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new AdNotFoundException("Объявление не найдено"));

        Comment comment = new Comment();
        comment.setText(createCommentDto.getText());
        //comment.setAuthor(author);
        comment.setAd(ad);
        comment.setCreatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);

        return commentMapper.toDto(savedComment);
    }
    //получение комментариев по айди объявления
    public CommentsResponseDto getCommentsByAdId(Long adId) {
        // Проверяем существование объявления
        if (!adRepository.existsById(adId)) {
            throw new AdNotFoundException("Объявление не найдено");
        }

        List<Comment> comments = commentRepository.findByAdIdOrderByCreatedAtDesc(adId);
        List<CommentResponseDto> commentDtos = comments.stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());

        CommentsResponseDto response = new CommentsResponseDto();
        response.setCount(commentDtos.size());
        response.setResults(commentDtos);

        return response;
    }
    //обновление объявления
    public CommentResponseDto updateComment(Long adId, Long commentId,
                                            CommentUpdateRequest updateRequest,
                                            User currentUser) {

        //проверка объявления
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new AdNotFoundException("Объявление не найдено"));

        //поиск комментарий
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Комментарий не найден"));

        //проверка, что комментарий принадлежит указанному объявлению
        if (!comment.getAd().getId().equals(adId)) {
            throw new CommentNotFoundException("Комментарий не принадлежит указанному объявлению");
        }

        //проверка права доступа (может редактировать только автор или администратор)
        if (!comment.getAuthor().getId().equals(currentUser.getId()) &&
                !currentUser.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("Нет прав для редактирования комментария");
        }

        //обновить текст комментария
        comment.setText(updateRequest.getText());
        comment.setUpdatedAt(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(comment);

        return commentMapper.toDto(updatedComment);
    }

    //удаление комментария
    public void deleteComment(Long adId, Long commentId, User currentUser) {
        //проверка существования объявления
        if (!adRepository.existsById(adId)) {
            throw new AdNotFoundException("Объявление не найдено");
        }

        //поиск комментария
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Комментарий не найден"));

        //проверяем, что комментарий принадлежит указанному объявлению
        if (!comment.getAd().getId().equals(adId)) {
            throw new CommentNotFoundException("Комментарий не принадлежит указанному объявлению");
        }

        //проверка прав доступа (может удалить только автор, автор объявления или администратор)
        boolean isAuthor = comment.getAuthor().getId().equals(currentUser.getId());
        boolean isAdAuthor = comment.getAd().getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().equals(Role.ADMIN);

        if (!isAuthor && !isAdAuthor && !isAdmin) {
            throw new AccessDeniedException("Нет прав для удаления комментария");
        }

        commentRepository.delete(comment);
    }
    //получение комментария по ID
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Комментарий не найден"));
    }
    //возможность удаления комментария
    public boolean canDeleteComment(Long commentId, User currentUser) {
        try {
            Comment comment = getCommentById(commentId);

            boolean isAuthor = comment.getAuthor().getId().equals(currentUser.getId());
            boolean isAdAuthor = comment.getAd().getAuthor().getId().equals(currentUser.getId());
            boolean isAdmin = currentUser.getRole().equals(Role.ADMIN);

            return isAuthor || isAdAuthor || isAdmin;
        } catch (CommentNotFoundException e) {
            return false;
        }
    }
    //получение комментариев пользователя
    public List<CommentResponseDto> getCommentsByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        List<Comment> comments = commentRepository.findByAuthorOrderByCreatedAtDesc(user);

        return comments.stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }
    public CommentsResponseDto getCommentsResponse(Long id) {
        List<Comment> comments = commentRepository.findByAdIdOrderByCreatedAtDesc(id);
        return commentMapper.toCommentsResponseDto(comments);
    }

}
