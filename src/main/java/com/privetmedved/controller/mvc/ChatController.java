package com.privetmedved.controller.mvc;

import com.privetmedved.dto.ChatForm;
import com.privetmedved.entity.Chat;
import com.privetmedved.entity.Message;
import com.privetmedved.entity.User;
import com.privetmedved.exception.ResourceNotFoundException;
import com.privetmedved.service.ChatService;
import com.privetmedved.service.UserService;
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
import java.util.Optional;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;

    public ChatController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }


    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String chatList(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
        List<Chat> chats = chatService.getUserChats(user.getId());
        model.addAttribute("chats", chats);
        return "chat/list";
    }
    @GetMapping("/{chatId}")
    @PreAuthorize("isAuthenticated()")
    public String chatView(@PathVariable Long chatId,
                           @RequestParam(defaultValue = "0") int page,
                           Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
        Chat chat = chatService.getChatById(chatId);
        Page<Message> messages = chatService.getChatMessages(chatId, page, 50);
        model.addAttribute("chat", chat);
        model.addAttribute("messages", messages.getContent());
        model.addAttribute("currentUser", user);
        return "chat/view";
    }
    @GetMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String createChatForm(Model model) {
        model.addAttribute("chatForm", new ChatForm());
        return "chat/create";
    }

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String createChat(@Valid @ModelAttribute ChatForm form,
                             BindingResult result,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "chat/create";
        }

        User currentUser = userService.findByUsername(principal.getName());
        User targetUser;
        try {
            targetUser = userService.findByUsername(form.getUsername());
        } catch (ResourceNotFoundException e) {
            result.rejectValue("username", "error.chatForm", "User not found");
            return "chat/create";
        }

        List<Chat> existingChats = chatService.getUserChats(currentUser.getId());
        Optional<Chat> directChat = existingChats.stream()
                .filter(c -> c.isDirect() && c.getParticipants().contains(targetUser))
                .findFirst();

        if (directChat.isPresent()) {
            return "redirect:/chat/" + directChat.get().getId();
        }

        Chat chat = chatService.createDirectChat(currentUser, targetUser,
                form.getName() != null && !form.getName().isBlank() ? form.getName() : targetUser.getUsername());

        if (chat.getId() == null || chat.getId() == 0) {
            redirectAttributes.addFlashAttribute("error", "Failed to create chat");
            return "redirect:/chat/list";
        }

        return "redirect:/chat/" + chat.getId();
    }
}
