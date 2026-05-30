package com.privetmedved.controller.mvc;

import com.privetmedved.dto.ProfileDto;
import com.privetmedved.dto.ProfileForm;
import com.privetmedved.dto.UserDto;
import com.privetmedved.entity.Profile;
import com.privetmedved.entity.User;
import com.privetmedved.service.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final ProfileService profileService;
    private final PostService postService;
    private final RoomService roomService;
    private final CommentService commentService;

    public ProfileController(UserService userService, ProfileService profileService,
                             PostService postService, RoomService roomService,
                             CommentService commentService) {
        this.userService = userService;
        this.profileService = profileService;
        this.postService = postService;
        this.roomService = roomService;
        this.commentService = commentService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String myProfile(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
        return redirectToProfile(user.getId(), model);
    }

    @GetMapping("/{userId}")
    public String viewProfile(@PathVariable Long userId, Model model, Principal principal) {
        return redirectToProfile(userId, model);
    }

    private String redirectToProfile(Long userId, Model model) {
        UserDto profileUser = userService.findById(userId);
        ProfileDto profile = profileService.findByUserId(userId);
        model.addAttribute("profileUser", profileUser);
        model.addAttribute("profile", profile);
        model.addAttribute("posts", postService.getPostsByAuthor(userId, 0, 10));
        model.addAttribute("rooms", roomService.findRoomsByCreator(userId));
        return "profile/view";
    }

    @GetMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public String editProfile(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
        Profile profile = profileService.getProfileEntity(user.getId());

        ProfileForm form = new ProfileForm();
        form.setDisplayName(profile.getDisplayName());
        form.setBio(profile.getBio());
        form.setBackgroundColor(profile.getBackgroundColor());
        form.setTextColor(profile.getTextColor());
        form.setAccentColor(profile.getAccentColor());
        form.setFontFamily(profile.getFontFamily());
        form.setCustomCss(profile.getCustomCss());
        form.setBackgroundImageUrl(profile.getBackgroundImageUrl());
        form.setProfileLayout(profile.getProfileLayout());

        model.addAttribute("profileForm", form);
        model.addAttribute("profile", profile);
        return "profile/edit";
    }

    @PostMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public String updateProfile(@Valid @ModelAttribute ProfileForm form,
                                BindingResult result,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "profile/edit";
        }
        User user = userService.findByUsername(principal.getName());
        profileService.updateProfile(user.getId(), form);
        userService.evictCache(user.getId());
        redirectAttributes.addFlashAttribute("success", "Profile updated!");
        return "redirect:/profile/" + user.getId();
    }
}