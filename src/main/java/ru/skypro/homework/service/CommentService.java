package ru.skypro.homework.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CreateOrUpdateCommentDto;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.utils.CommentMapper;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final CommentMapper mapper;

    public CommentService(CommentRepository commentRepository,
                          AdRepository adRepository,
                          UserRepository userRepository,
                          CommentMapper mapper) {
        this.commentRepository = commentRepository;
        this.adRepository = adRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Transactional
    public CommentDto addComment(Long adId, CreateOrUpdateCommentDto dto, String username) {
        Ad ad = adRepository.findById(adId).orElseThrow(() -> new RuntimeException("Ad not found"));
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment c = new Comment();
        c.setText(dto.getText());
        c.setAd(ad);
        c.setAuthor(author);
        c.setCreatedAt(System.currentTimeMillis());

        Comment saved = commentRepository.save(c);
        return mapper.toDto(saved);
    }
}
