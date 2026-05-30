package com.privetmedved.controller.mvc;

import com.privetmedved.dto.RegisterForm;
import com.privetmedved.entity.Role;
import com.privetmedved.entity.User;
import com.privetmedved.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("loginError", "Invalid username or password");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterForm form,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auth/register";
        }

        if (!form.getPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
            return "auth/register";
        }

        try {
            userService.register(form);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please log in.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            result.rejectValue("username", "error.username", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/profile")
    public String profilePage(Principal principal, Model model) {
        User user = userService.findByUsernameWithRoles(principal.getName());
        model.addAttribute("user", user);
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().equals(Role.ADMIN));
        boolean isModerator = user.getRoles().stream().anyMatch(r -> r.getName().equals(Role.MODERATOR));
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isModerator", isModerator);
        return "profile/index";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminPage(Model model) {
        model.addAttribute("users", userService.findActiveUsers());
        return "admin/dashboard";
    }
}