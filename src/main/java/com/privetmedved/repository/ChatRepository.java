package com.privetmedved.repository;

import com.privetmedved.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT c FROM Chat c JOIN c.participants p WHERE p.id = :userId")
    List<Chat> findByParticipantId(@Param("userId") Long userId);

    @Query("SELECT c FROM Chat c LEFT JOIN FETCH c.participants WHERE c.id = :id")
    Optional<Chat> findByIdWithParticipants(@Param("id") Long id);

    @Query("SELECT c FROM Chat c WHERE c.room.id = :roomId AND c.isDirect = false")
    List<Chat> findRoomChats(@Param("roomId") Long roomId);
}