package com.privetmedved.controller.mvc;

import com.privetmedved.dto.PostForm;
import com.privetmedved.dto.PostDto;
import com.privetmedved.entity.Post;
import com.privetmedved.entity.Room;
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
import java.util.List;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;
    private final PostService postService;
    private final UserService userService;


    public RoomController(RoomService roomService, PostService postService, UserService userService) {



        this.roomService = roomService;
        this.postService = postService;

        this.userService = userService;
    }

    @GetMapping
    public String listRooms(Model model) {
        List<Room> publicRooms = roomService.findPublicRooms();
        List<Room> popular = roomService.findMostPopularRooms();

        model.addAttribute("rooms", publicRooms);

        model.addAttribute("popularRooms", popular);
        return "room/list";
    }

    @GetMapping("/my")
    public String myRooms(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
        List<Room> created = roomService.findRoomsByCreator(user.getId());
        List<Room> joined = roomService.findRoomsByParticipant(user.getId());
        model.addAttribute("createdRooms", created);
        model.addAttribute("joinedRooms", joined);
        return "room/my-rooms";
    }

    @GetMapping("/{slug}")
    public String roomPage(@PathVariable String slug,

                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           Principal principal,
                           Model model) {
        Room room = roomService.findBySlug(slug);
        Page<PostDto> posts = postService.getPostsByRoom(room.getId(), page, size);

        model.addAttribute("room", room);
        model.addAttribute("posts", posts);
        model.addAttribute("postForm", new PostForm());
        model.addAttribute("isMember", principal != null &&
                room.getParticipants().stream()
                        .anyMatch(u -> u.getUsername().equals(principal.getName())));
        return "room/view";
    }

    @GetMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String createRoomPage(Model model) {
        model.addAttribute("roomForm", new com.privetmedved.dto.RoomForm());
        return "room/create";
    }

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String createRoom(@Valid @ModelAttribute("roomForm") com.privetmedved.dto.RoomForm form,
                             BindingResult result,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "room/create";
        }

        User user = userService.findByUsername(principal.getName());
        Room room = roomService.createRoom(form, user);
        redirectAttributes.addFlashAttribute("success", "Room created!");
        return "redirect:/rooms/" + room.getSlug();
    }

    @PostMapping("/{slug}/join")
    @PreAuthorize("isAuthenticated()")
    public String joinRoom(@PathVariable String slug, Principal principal,
                           RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(principal.getName());
        try {
            roomService.joinRoom(slug, user);
            redirectAttributes.addFlashAttribute("success", "Joined room!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/rooms/" + slug;
    }

    @PostMapping("/{slug}/leave")
    @PreAuthorize("isAuthenticated()")
    public String leaveRoom(@PathVariable String slug, Principal principal,
                            RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(principal.getName());
        try {
            roomService.leaveRoom(slug, user);
            redirectAttributes.addFlashAttribute("success", "Left room");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/rooms/" + slug;
    }

    @GetMapping("/{slug}/edit")
    @PreAuthorize("isAuthenticated()")
    public String editRoomPage(@PathVariable String slug, Principal principal, Model model) {
        Room room = roomService.findBySlug(slug);
        User user = userService.findByUsername(principal.getName());

        if (!room.getCreator().getId().equals(user.getId())) {
            return "redirect:/rooms/" + slug;
        }

        com.privetmedved.dto.RoomForm form = new com.privetmedved.dto.RoomForm();
        form.setName(room.getName());
        form.setSlug(room.getSlug());
        form.setDescription(room.getDescription());
        form.setPrivate(room.isPrivate());
        form.setMaxParticipants(room.getMaxParticipants());

        model.addAttribute("roomForm", form);
        model.addAttribute("room", room);
        return "room/edit";
    }

    @PostMapping("/{slug}/edit")
    @PreAuthorize("isAuthenticated()")
    public String editRoom(@PathVariable String slug,
                           @Valid @ModelAttribute("roomForm") com.privetmedved.dto.RoomForm form,
                           BindingResult result,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "room/edit";
        }

        User user = userService.findByUsername(principal.getName());
        Room room = roomService.findBySlug(slug);

        if (!room.getCreator().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized");
            return "redirect:/rooms/" + slug;
        }

        roomService.updateRoom(room.getId(), form, user);
        redirectAttributes.addFlashAttribute("success", "Room updated!");
        return "redirect:/rooms/" + room.getSlug();
    }

    @PostMapping("/{slug}/delete")
    @PreAuthorize("isAuthenticated()")
    public String deleteRoom(@PathVariable String slug, Principal principal,
                             RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(principal.getName());
        Room room = roomService.findBySlug(slug);

        if (!room.getCreator().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized");
            return "redirect:/rooms/" + slug;
        }

        roomService.deleteRoom(room.getId(), user);
        redirectAttributes.addFlashAttribute("success", "Room deleted");
        return "redirect:/rooms";
    }
}