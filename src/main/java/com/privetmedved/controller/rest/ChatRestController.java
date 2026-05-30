package com.privetmedved.controller.rest;

import com.privetmedved.dto.*;
import com.privetmedved.entity.Chat;
import com.privetmedved.entity.Message;
import com.privetmedved.entity.User;
import com.privetmedved.service.ChatService;
import com.privetmedved.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@Tag(name = "Chat", description = "Chat and messaging API")
public class ChatRestController {

    private final ChatService chatService;
    private final UserService userService;

    public ChatRestController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Chat>> getUserChats(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return ResponseEntity.ok(chatService.getUserChats(user.getId()));
    }

    @PostMapping("/{chatId}/messages")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Message> sendMessage(@PathVariable Long chatId,
                                                @RequestBody MessageForm form,
                                                Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return ResponseEntity.ok(chatService.sendMessage(chatId, form.getContent(), user));
    }

    @GetMapping("/{chatId}/messages")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Message>> getMessages(@PathVariable Long chatId,
                                                      @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(chatService.getChatMessages(chatId, page, 50).getContent());
    }
}