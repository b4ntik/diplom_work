package ru.skypro.homework.security;

import org.springframework.stereotype.Service;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;

@Service
public class SecurityService {

    private final AdRepository adRepository;
    private final CommentRepository commentRepository;

    public SecurityService(AdRepository adRepository,
                           CommentRepository commentRepository) {
        this.adRepository = adRepository;
        this.commentRepository = commentRepository;
    }

    public boolean isAdOwner(Long adId) {
        User user = getCurrentUser();
        return adRepository.findById(adId)
                .map(ad -> ad.getAuthor().getId().equals(user.getId()))
                .orElse(false);
    }

    public boolean isCommentOwner(Long commentId) {
        User user = getCurrentUser();
        return commentRepository.findById(commentId)
                .map(c -> c.getAuthor().getId().equals(user.getId()))
                .orElse(false);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }
}