package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entity.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Найти все комментарии для объявления, отсортированные по дате
    List<Comment> findByAdIdOrderByCreatedAtDesc(Long adId);

    // Найти комментарий по ID и ID объявления
    @Query("SELECT c FROM Comment c WHERE c.id = :commentId AND c.ad.id = :adId")
    Optional<Comment> findByIdAndAdId(@Param("commentId") Long commentId,
                                      @Param("adId") Long adId);

    // Удалить комментарий по ID и ID объявления
    @Query("DELETE FROM Comment c WHERE c.id = :commentId AND c.ad.id = :adId")
    void deleteByIdAndAdId(@Param("commentId") Long commentId,
                           @Param("adId") Long adId);

    // Проверить существование комментария в объявлении
    boolean existsByIdAndAdId(Long commentId, Long adId);

    // Подсчитать количество комментариев для объявления
    long countByAdId(Long adId);

    // Найти комментарий по ID и ID объявления
   // List<Comment> findByIdAndAdId(Long commentId, Long adId);
}