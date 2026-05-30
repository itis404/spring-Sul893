package com.privetmedved.repository;

import com.privetmedved.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, CustomPostRepository {

    Page<Post> findByRoomIdOrderByPinnedDescCreatedAtDesc(Long roomId, Pageable pageable);

    Page<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.author LEFT JOIN FETCH p.room " +
           "ORDER BY p.createdAt DESC")
    List<Post> findRecentPosts(Pageable pageable);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.author " +
           "WHERE p.room.id = :roomId ORDER BY p.pinned DESC, p.createdAt DESC")
    Page<Post> findByRoomIdWithAuthor(@Param("roomId") Long roomId, Pageable pageable);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.author " +
           "WHERE p.id = :id")
    Post findByIdWithAuthor(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Post p SET p.voteCount = p.voteCount + :delta WHERE p.id = :id")
    void updateVoteCount(@Param("id") Long id, @Param("delta") int delta);

    @Query("SELECT p FROM Post p WHERE SIZE(p.comments) > :minComments " +
           "ORDER BY p.voteCount DESC")
    List<Post> findHotPosts(@Param("minComments") int minComments);

    @Query("SELECT p FROM Post p LEFT JOIN p.tags t WHERE t.name IN :tagNames " +
           "GROUP BY p HAVING COUNT(DISTINCT t) = :tagCount")
    List<Post> findByAllTags(@Param("tagNames") List<String> tagNames,
                             @Param("tagCount") long tagCount, Pageable pageable);

    @Query(value = "SELECT * FROM posts p WHERE p.id IN " +
           "(SELECT pc.post_id FROM post_tags pc JOIN tags t ON pc.tag_id = t.id WHERE t.name = :tagName)",
           nativeQuery = true)
    List<Post> findByTagNameNative(@Param("tagName") String tagName);
}