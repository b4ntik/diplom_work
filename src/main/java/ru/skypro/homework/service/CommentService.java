package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor

public class CommentService {

    private final CommentRepository commentRepository;
    private final AdRepository adRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByAdId(Long adId) {
        log.info("Получение комментариев для adId: {}", adId);
        try {
            if (!adRepository.existsById(adId)) {
                throw new RuntimeException("Ad not found with id: " + adId);
            }

            List<Comment> comments = commentRepository.findByAdIdOrderByCreatedAtDesc(adId);
            return commentMapper.toDtoList(comments);
        } catch  (Exception e) {
            log.error ("Ошибка получения комментариев: {}", e.getMessage());

            if(e.getMessage().contains("нет доступа") || e.getMessage().contains("permission denied")){
                log.error("ОШИБКА ПРАВ ДОСТУПА К ТАБЛИЦЕ COMMENTS!");
                log.error("Проверьте права пользователя БД на таблицу comments");

                // Возвращаем пустой список вместо ошибки
                return Collections.emptyList();
            }
            throw new RuntimeException("Ошибка при получении комментариев: " + e.getMessage());
        }
    }

    @Transactional
    public CommentDto createComment(Long adId, String text, String username) {
        log.debug("Создание комментария для объявления ID: {}, пользователь: {}", adId, username);

        // Находим объявление
        log.info("Поиск объявления: ID={}", adId);
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> {
                    log.error("Объявление не найдено: ID={}", adId);

                    // Дополнительная проверка - сколько всего объявлений в базе
                    long totalAds = adRepository.count();
                    log.error("Всего объявлений в базе: {}", totalAds);

                    // Выводим все ID объявлений для отладки
                    List<Long> allAdIds = adRepository.findAll().stream()
                            .map(Ad::getId)
                            .collect(Collectors.toList());
                    log.error("Все ID объявлений в базе: {}", allAdIds);

                    return new RuntimeException("Объявление не найдено с ID: " + adId);
                });
        log.info("Объявление найдено: ID={}, title={}", ad.getId(), ad.getTitle());


        // Находим пользователя
        log.info("Поиск пользователя: {}", username);
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + username));
        log.info("Пользователь найден: ID={}", author.getId());

        // Проверяем, не является ли пользователь автором объявления
        // (обычно это разрешено, но можно добавить проверку если нужно)
        if (ad.getAuthor().getId().equals(author.getId())) {
            log.debug("Комментарий создается автором объявления");
        }
        // Создаем комментарий
        log.info("Создание объекта комментария");
        Comment comment = new Comment();
        comment.setText(text);
        comment.setAd(ad);
        comment.setAuthor(author);
        comment.setCreatedAt(LocalDateTime.now());

        // Сохраняем в базе
        log.info("Сохранение комментария в базу");
        Comment savedComment = commentRepository.save(comment);
        log.debug("Комментарий сохранен с ID: {}", savedComment.getId());

        // Преобразуем в DTO
        return commentMapper.toDto(savedComment);
    }
    public void deleteComment(Long adId, Long commentId, String username) {
        log.debug("Удаление комментария: adId={}, commentId={}, user={}",
                adId, commentId, username);

        // 1. Проверяем существование объявления
        if (!adRepository.existsById(adId)) {
            throw new RuntimeException("Объявление не найдено с ID: " + adId);
        }

        // 2. Находим пользователя
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + username));

        // 3. Находим комментарий
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Комментарий не найден с ID: " + commentId));

        // 4. Проверяем, что комментарий относится к указанному объявлению
        if (!comment.getAd().getId().equals(adId)) {
            throw new RuntimeException(
                    String.format("Комментарий %s не принадлежит объявлению %s", commentId, adId)
            );
        }

        // 5. Проверяем права доступа
        // Пользователь может удалить свой комментарий ИЛИ быть автором объявления
        boolean isCommentAuthor = comment.getAuthor().getId().equals(user.getId());
        boolean isAdAuthor = comment.getAd().getAuthor().getId().equals(user.getId());

        if (!isCommentAuthor && !isAdAuthor) {
            log.warn("Попытка удалить чужой комментарий. User: {}, Comment author: {}, Ad author: {}",
                    user.getId(), comment.getAuthor().getId(), comment.getAd().getAuthor().getId());
            throw new RuntimeException("Вы не можете удалить этот комментарий");
        }

        // 6. Удаляем комментарий
        commentRepository.delete(comment);
        log.debug("Комментарий {} удален пользователем {}", commentId, username);
    }
    public CommentDto updateComment(Long adId, Long commentId, String newText, String username) {
        log.debug("Обновление комментария: adId={}, commentId={}, user={}",
                adId, commentId, username);

        // 1. Валидация текста
        if (newText == null || newText.trim().isEmpty()) {
            throw new RuntimeException("Текст комментария не может быть пустым");
        }
        String trimmedText = newText.trim();

        // 2. Проверяем существование объявления
        if (!adRepository.existsById(adId)) {
            throw new RuntimeException("Объявление не найдено с ID: " + adId);
        }

        // 3. Находим пользователя
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + username));

        // 4. Находим комментарий
        Comment comment = commentRepository.findByIdAndAdId(commentId, adId)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Комментарий %s не найден в объявлении %s", commentId, adId)
                ));

        // 5. Проверяем права доступа (только автор может редактировать)
        if (!comment.getAuthor().getId().equals(user.getId())) {
            log.warn("Попытка редактирования чужого комментария. User: {}, Comment author: {}",
                    user.getId(), comment.getAuthor().getId());
            throw new RuntimeException("Вы можете редактировать только свои комментарии");
        }

        // 6. Проверяем, не пытается ли пользователь установить тот же текст
        if (comment.getText().equals(trimmedText)) {
            log.warn("Пользователь пытается установить тот же текст комментария");
            // Можно просто вернуть существующий комментарий или выбросить исключение
            // return commentMapper.toDto(comment);
        }

        // 7. Обновляем комментарий
        comment.setText(trimmedText);
        comment.setUpdatedAt(LocalDateTime.now()); // Если есть поле для времени обновления

        Comment updatedComment = commentRepository.save(comment);
        log.debug("Комментарий обновлен: {}", commentId);

        return commentMapper.toDto(updatedComment);
    }
}

