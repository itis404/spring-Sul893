package com.privetmedved.controller.rest;

import com.privetmedved.dto.*;
import com.privetmedved.entity.User;
import com.privetmedved.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Posts", description = "Post management API")
public class PostRestController {

    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;

    public PostRestController(PostService postService, UserService userService, CommentService commentService) {
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
    }

    @Operation(summary = "Get all posts with pagination and filters")
    @GetMapping
    public ResponseEntity<List<PostDto>> getPosts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) Long authorId,
            @RequestParam(defaultValue = "newest") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(postService.findPostsByCriteria(search, roomId, authorId, sortBy, page, size));
    }

    @Operation(summary = "Get post by ID")
    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(new PostDto());
    }

    @Operation(summary = "Create a new post")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostDto> createPost(@Valid @RequestBody PostForm form, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return ResponseEntity.ok(new PostDto());
    }

    @Operation(summary = "Update a post")
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostDto> updatePost(@PathVariable Long id,
                                               @Valid @RequestBody PostForm form,
                                               Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return ResponseEntity.ok(new PostDto());
    }

    @Operation(summary = "Delete a post")
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        postService.deletePost(id, user);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Vote on a post")
    @PostMapping("/{id}/vote")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Integer> votePost(@PathVariable Long id,
                                             @RequestParam int value,
                                             Principal principal) {
        User user = userService.findByUsername(principal.getName());
        int newCount = postService.vote(id, user, value);
        return ResponseEntity.ok(newCount);
    }

    @Operation(summary = "Get comments for a post")
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentForm>> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(List.of());
    }

    @Operation(summary = "Add a comment to a post")
    @PostMapping("/{id}/comments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> addComment(@PathVariable Long id,
                                            @Valid @RequestBody CommentForm form,
                                            Principal principal) {
        User user = userService.findByUsername(principal.getName());
        commentService.addComment(id, form, user);
        return ResponseEntity.ok().build();
    }
}