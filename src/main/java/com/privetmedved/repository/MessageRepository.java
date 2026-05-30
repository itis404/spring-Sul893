package com.privetmedved.repository;

import com.privetmedved.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByChatIdOrderBySentAtDesc(Long chatId, Pageable pageable);

    @Query("SELECT m FROM Message m LEFT JOIN FETCH m.sender " +
           "WHERE m.chat.id = :chatId ORDER BY m.sentAt DESC")
    List<Message> findLatestMessages(@Param("chatId") Long chatId, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId " +
           "AND m.sentAt > (SELECT MAX(m2.sentAt) FROM Message m2 " +
           "WHERE m2.chat.id = :chatId AND m2.sender.id = :userId AND m2.isEdited = false)")
    List<Message> findNewMessagesSinceLastEdit(@Param("chatId") Long chatId,
                                                @Param("userId") Long userId);
}