package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entity.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Найти все комментарии для объявления, отсортированные по дате
    List<Comment> findByAdIdOrderByCreatedAtDesc(Long adId);

    // Кастомный запрос с пагинацией
    @Query("SELECT c FROM Comment c WHERE c.ad.id = :adId ORDER BY c.createdAt DESC")
    List<Comment> findCommentsByAdId(@Param("adId") Long adId);

    // Подсчитать количество комментариев для объявления
    long countByAdId(Long adId);

    // Найти комментарий по ID и ID объявления
    List<Comment> findByIdAndAdId(Long commentId, Long adId);
}