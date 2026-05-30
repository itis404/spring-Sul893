package com.privetmedved.service;

import com.privetmedved.dto.PostForm;
import com.privetmedved.dto.PostDto;
import com.privetmedved.entity.*;
import com.privetmedved.converter.PostConverter;
import com.privetmedved.exception.BadRequestException;
import com.privetmedved.exception.ResourceNotFoundException;
import com.privetmedved.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final RoomRepository roomRepository;
    private final TagRepository tagRepository;
    private final VoteRepository voteRepository;
    private final CommentRepository commentRepository;
    private final PostConverter postConverter;

    @Transactional(readOnly = true)
    @Cacheable(value = "posts", key = "#id")
    public PostDto findById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", id));
        return postConverter.toDto(post);
    }

    @Transactional
    @CacheEvict(value = "posts", key = "#result.id")
    public Post createPost(PostForm form, User author) {
        Room room = roomRepository.findById(form.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room", form.getRoomId()));

        Post post = new Post();
        post.setTitle(form.getTitle());
        post.setContent(form.getContent());
        post.setAuthor(author);
        post.setRoom(room);

        if (form.getTags() != null && !form.getTags().isEmpty()) {
            Set<Tag> tags = form.getTags().stream()
                    .map(tagName -> tagRepository.findByName(tagName)
                            .orElseGet(() -> {
                                Tag newTag = new Tag(tagName);
                                return tagRepository.save(newTag);
                            }))
                    .collect(Collectors.toSet());
            post.setTags(tags);

            tags.forEach(tag -> {
                tag.setUsageCount(tag.getUsageCount() + 1);
                tagRepository.save(tag);
            });
        }

        Post saved = postRepository.save(post);
        log.info("Post created: {} by {}", saved.getTitle(), author.getUsername());
        return saved;
    }

    @Transactional(readOnly = true)
    public Post getPostEntity(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", id));
    }

    @Transactional
    @CacheEvict(value = "posts", key = "#id")
    public Post updatePost(Long id, PostForm form, User currentUser) {
        Post post = getPostEntity(id);

        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only edit your own posts");
        }

        post.setTitle(form.getTitle());
        post.setContent(form.getContent());
        post.setUpdatedAt(LocalDateTime.now());

        Post saved = postRepository.save(post);
        log.debug("Post updated: {}", id);
        return saved;
    }

    @Transactional
    @CacheEvict(value = "posts", key = "#id")
    public void deletePost(Long id, User currentUser) {
        Post post = getPostEntity(id);

        boolean isOwner = post.getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName().equals(Role.ADMIN));
        boolean isModerator = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName().equals(Role.MODERATOR));

        if (!isOwner && !isAdmin && !isModerator) {
            throw new BadRequestException("You cannot delete this post");
        }

        postRepository.delete(post);
        log.info("Post deleted: {}", id);
    }

    @Transactional(readOnly = true)
    public Page<PostDto> getPostsByRoom(Long roomId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findByRoomIdWithAuthor(roomId, pageable);
        return posts.map(postConverter::toDto);
    }

    @Transactional(readOnly = true)
    public Page<PostDto> getPostsByAuthor(Long authorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findByAuthorIdOrderByCreatedAtDesc(authorId, pageable);
        return posts.map(postConverter::toDto);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getRecentPosts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Post> posts = postRepository.findRecentPosts(pageable);
        return posts.stream().map(postConverter::toDto).collect(Collectors.toList());
    }

    @Transactional
    public int vote(Long postId, User user, int value) {
        Post post = getPostEntity(postId);

        voteRepository.findByUserIdAndPostId(user.getId(), postId).ifPresentOrElse(
            vote -> {
                if (vote.getValue() == value) {
                    voteRepository.delete(vote);
                    int delta = -value;
                    post.setVoteCount(post.getVoteCount() + delta);
                    postRepository.updateVoteCount(postId, delta);
                } else {
                    vote.setValue(value);
                    voteRepository.save(vote);
                    int delta = value * 2;
                    post.setVoteCount(post.getVoteCount() + delta);
                    postRepository.updateVoteCount(postId, delta);
                }
            },
            () -> {
                Vote vote = new Vote();
                vote.setUser(user);
                vote.setPost(post);
                vote.setValue(value);
                voteRepository.save(vote);
                post.setVoteCount(post.getVoteCount() + value);
                postRepository.updateVoteCount(postId, value);
            }
        );

        return postRepository.findById(postId).orElse(post).getVoteCount();
    }

    public long getCommentCount(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    public List<PostDto> findPostsByCriteria(String search, Long roomId, Long authorId,
                                              String sortBy, int page, int size) {
        return postRepository.findPostsByCriteria(search, roomId, authorId, sortBy, page, size)
                .stream().map(postConverter::toDto).collect(Collectors.toList());
    }

    public List<PostDto> findMostDiscussedPosts(int limit) {
        return postRepository.findMostDiscussedPosts(limit)
                .stream().map(postConverter::toDto).collect(Collectors.toList());
    }
}
