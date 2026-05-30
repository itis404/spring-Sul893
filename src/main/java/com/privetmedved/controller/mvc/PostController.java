package com.privetmedved.controller.mvc;

import com.privetmedved.dto.PostForm;
import com.privetmedved.dto.PostDto;
import com.privetmedved.entity.Post;
import com.privetmedved.entity.User;
import com.privetmedved.service.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;
    private final RoomService roomService;

    public PostController(PostService postService, UserService userService,
                          CommentService commentService, RoomService roomService) {
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
        this.roomService = roomService;
    }

    @GetMapping
    public String listPosts(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "20") int size,
                            Model model) {
        model.addAttribute("posts", postService.getRecentPosts(size));
        model.addAttribute("mostDiscussed", postService.findMostDiscussedPosts(5));
        model.addAttribute("rooms", roomService.findPublicRooms());
        return "post/list";
    }

    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model, Principal principal) {
        Post post = postService.getPostEntity(id);
        model.addAttribute("post", post);
        model.addAttribute("comments", commentService.getCommentsByPost(id));
        model.addAttribute("postDto", postService.findById(id) != null ?
                new PostDto() : null);
        model.addAttribute("commentForm", new com.privetmedved.dto.CommentForm());
        model.addAttribute("canEdit", principal != null &&
                post.getAuthor().getUsername().equals(principal.getName()));
        return "post/view";
    }

    @GetMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String createPostPage(@RequestParam(required = false) Long roomId, Model model) {
        PostForm form = new PostForm();
        form.setRoomId(roomId);
        model.addAttribute("postForm", form);
        model.addAttribute("rooms", roomService.findPublicRooms());
        return "post/create";
    }

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String createPost(@Valid @ModelAttribute PostForm form,
                             BindingResult result,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "post/create";
        }

        User user = userService.findByUsername(principal.getName());
        Post post = postService.createPost(form, user);
        redirectAttributes.addFlashAttribute("success", "Post created!");
        return "redirect:/posts/" + post.getId();
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("isAuthenticated()")
    public String editPostPage(@PathVariable Long id, Principal principal, Model model) {
        Post post = postService.getPostEntity(id);

        if (!post.getAuthor().getUsername().equals(principal.getName())) {
            return "redirect:/posts/" + id;
        }

        PostForm form = new PostForm();
        form.setTitle(post.getTitle());
        form.setContent(post.getContent());
        form.setRoomId(post.getRoom().getId());

        model.addAttribute("postForm", form);
        model.addAttribute("post", post);
        return "post/edit";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("isAuthenticated()")
    public String editPost(@PathVariable Long id,
                           @Valid @ModelAttribute PostForm form,
                           BindingResult result,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "post/edit";
        }

        User user = userService.findByUsername(principal.getName());
        postService.updatePost(id, form, user);
        redirectAttributes.addFlashAttribute("success", "Post updated!");
        return "redirect:/posts/" + id;
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("isAuthenticated()")
    public String deletePost(@PathVariable Long id, Principal principal,
                             RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(principal.getName());
        Post post = postService.getPostEntity(id);
        String roomSlug = post.getRoom().getSlug();

        postService.deletePost(id, user);
        redirectAttributes.addFlashAttribute("success", "Post deleted");
        return "redirect:/rooms/" + roomSlug;
    }
}