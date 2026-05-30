package com.privetmedved.repository;

import com.privetmedved.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Query("SELECT r FROM Room r LEFT JOIN FETCH r.tags WHERE r.slug = :slug")
    Optional<Room> findBySlugWithTags(@Param("slug") String slug);

    @Query("SELECT r FROM Room r LEFT JOIN FETCH r.creator LEFT JOIN FETCH r.tags " +
           "WHERE r.isPrivate = false ORDER BY r.createdAt DESC")
    List<Room> findPublicRooms();

    @Query("SELECT r FROM Room r WHERE SIZE(r.participants) = " +
           "(SELECT MAX(SIZE(r2.participants)) FROM Room r2)")
    List<Room> findMostPopularRooms();

    @Query("SELECT r FROM Room r LEFT JOIN r.tags t WHERE t.name = :tagName")
    List<Room> findByTagName(@Param("tagName") String tagName);

    @Query("SELECT r FROM Room r WHERE r.creator.id = :userId")
    List<Room> findByCreatorId(@Param("userId") Long userId);

    @Query("SELECT r FROM Room r JOIN r.participants p WHERE p.id = :userId")
    List<Room> findByParticipantId(@Param("userId") Long userId);
}