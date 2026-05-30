package com.privetmedved.service;

import com.privetmedved.dto.CommentForm;
import com.privetmedved.entity.Comment;
import com.privetmedved.entity.Post;
import com.privetmedved.entity.User;
import com.privetmedved.exception.BadRequestException;
import com.privetmedved.exception.ResourceNotFoundException;
import com.privetmedved.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;

    @Transactional
    public Comment addComment(Long postId, CommentForm form, User author) {
        Post post = postService.getPostEntity(postId);
        if (post == null) {
            throw new ResourceNotFoundException("Post", postId);
        }

        Comment comment = new Comment();
        comment.setContent(form.getContent());
        comment.setPost(post);
        comment.setAuthor(author);

        if (form.getParentId() != null) {
            Comment parent = commentRepository.findById(form.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comment", form.getParentId()));
            comment.setParent(parent);
        }

        Comment saved = commentRepository.save(comment);
        return saved;
    }

    @Transactional
    public Comment updateComment(Long commentId, CommentForm form, User currentUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));

        if (!comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only edit your own comments");
        }

        comment.setContent(form.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        Comment saved = commentRepository.save(comment);
        return saved;
    }

    @Transactional
    public void deleteComment(Long commentId, User currentUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));

        if (!comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPost(Long postId) {
        return commentRepository.findByPostIdWithAuthor(postId);
    }

    @Transactional(readOnly = true)
    public List<Comment> getRecentCommentsByUser(Long userId) {
        return commentRepository.findRecentByUserId(userId);
    }
}
