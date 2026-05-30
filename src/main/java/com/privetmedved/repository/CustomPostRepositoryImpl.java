package com.privetmedved.repository;

import com.privetmedved.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.util.List;
import java.util.Optional;

public class CustomPostRepositoryImpl implements CustomPostRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Post> findPostsByCriteria(String search, Long roomId, Long authorId,
                                           String sortBy, int page, int size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Post> cq = cb.createQuery(Post.class);
        Root<Post> post = cq.from(Post.class);

        Join<Post, User> author = post.join("author", JoinType.LEFT);
        Join<Post, Room> room = post.join("room", JoinType.LEFT);

        Predicate predicate = cb.conjunction();

        if (search != null && !search.isBlank()) {
            String pattern = "%" + search.toLowerCase() + "%";
            predicate = cb.and(predicate, cb.or(
                cb.like(cb.lower(post.get("title")), pattern),
                cb.like(cb.lower(post.get("content")), pattern)
            ));
        }

        if (roomId != null) {
            predicate = cb.and(predicate, cb.equal(room.get("id"), roomId));
        }

        if (authorId != null) {
            predicate = cb.and(predicate, cb.equal(author.get("id"), authorId));
        }

        cq.where(predicate);

        if ("votes".equals(sortBy)) {
            cq.orderBy(cb.desc(post.get("voteCount")));
        } else if ("comments".equals(sortBy)) {
            cq.orderBy(cb.desc(cb.size(post.get("comments"))));
        } else {
            cq.orderBy(cb.desc(post.get("pinned")), cb.desc(post.get("createdAt")));
        }

        TypedQuery<Post> query = em.createQuery(cq);
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    @Override
    public Optional<Post> findPostWithDetails(Long id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Post> cq = cb.createQuery(Post.class);
        Root<Post> post = cq.from(Post.class);

        post.fetch("author", JoinType.LEFT);
        post.fetch("room", JoinType.LEFT);
        post.fetch("tags", JoinType.LEFT);

        cq.where(cb.equal(post.get("id"), id));

        try {
            Post result = em.createQuery(cq).getSingleResult();
            return Optional.ofNullable(result);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Post> findMostDiscussedPosts(int limit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Post> cq = cb.createQuery(Post.class);
        Root<Post> post = cq.from(Post.class);

        post.fetch("author", JoinType.LEFT);
        post.fetch("room", JoinType.LEFT);

        Subquery<Long> subquery = cq.subquery(Long.class);
        Root<Comment> comment = subquery.from(Comment.class);
        subquery.select(cb.count(comment.get("id")));
        subquery.where(cb.equal(comment.get("post"), post));

        cq.orderBy(cb.desc(subquery));
        return em.createQuery(cq).setMaxResults(limit).getResultList();
    }
}