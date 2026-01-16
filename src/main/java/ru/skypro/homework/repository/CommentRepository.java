package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.entity.User;

import javax.transaction.Transactional;
import java.util.List;

@Repository

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByAdIdOrderByCreatedAtDesc(Long adId);
    List<Comment> findByAuthorOrderByCreatedAtDesc(User author);
    boolean existsByIdAndAdId(Long id, Long adId);

    @Transactional
    void deleteByAdId(Long adId);

}