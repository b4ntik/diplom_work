package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.User;

import java.util.List;

public interface AdRepository extends JpaRepository<Ad, Long> {
    List<Ad> findByAuthor(User user);

    List<Ad> findAllByOrderByCreatedAtDesc();

    List<Ad> findByAuthorIdOrderByCreatedAtDesc(Long authorId);

    List<Ad> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String title);
}
