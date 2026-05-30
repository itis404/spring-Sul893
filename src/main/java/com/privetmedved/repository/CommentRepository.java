package com.privetmedved.repository;

import com.privetmedved.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);

    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.author WHERE c.post.id = :postId " +
           "ORDER BY c.createdAt DESC")
    List<Comment> findByPostIdWithAuthor(@Param("postId") Long postId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId")
    long countByPostId(@Param("postId") Long postId);

    @Query("SELECT c FROM Comment c WHERE c.author.id = :userId ORDER BY c.createdAt DESC")
    List<Comment> findRecentByUserId(@Param("userId") Long userId);
}