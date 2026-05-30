package com.privetmedved.repository;

import com.privetmedved.entity.Post;

import java.util.List;
import java.util.Optional;

public interface CustomPostRepository {

    List<Post> findPostsByCriteria(String search, Long roomId, Long authorId,
                                   String sortBy, int page, int size);

    Optional<Post> findPostWithDetails(Long id);

    List<Post> findMostDiscussedPosts(int limit);
}