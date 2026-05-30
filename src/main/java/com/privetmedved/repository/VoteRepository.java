package com.privetmedved.repository;

import com.privetmedved.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByUserIdAndPostId(Long userId, Long postId);

    @Query("SELECT COALESCE(SUM(v.value), 0) FROM Vote v WHERE v.post.id = :postId")
    int sumVotesByPostId(@Param("postId") Long postId);
}