package com.privetmedved.service;

import com.privetmedved.entity.Chat;
import com.privetmedved.entity.Message;
import com.privetmedved.entity.User;
import com.privetmedved.exception.BadRequestException;
import com.privetmedved.exception.ResourceNotFoundException;
import com.privetmedved.repository.ChatRepository;
import com.privetmedved.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    public Chat getChatById(Long id) {
        return chatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chat", id));
    }


    @Transactional(readOnly = true)
    public List<Chat> getUserChats(Long userId) {
        return chatRepository.findByParticipantId(userId);
    }

    @Transactional
    public Message sendMessage(Long chatId, String content, User sender) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat", chatId));

        if (!chat.getParticipants().contains(sender)) {
            throw new BadRequestException("You are not a participant of this chat");
        }

        Message message = new Message();
        message.setContent(content);
        message.setChat(chat);
        message.setSender(sender);

        Message saved = messageRepository.save(message);
        return saved;
    }

    @Transactional(readOnly = true)
    public Page<Message> getChatMessages(Long chatId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return messageRepository.findByChatIdOrderBySentAtDesc(chatId, pageable);
    }

    @Transactional
    public Message updateMessage(Long messageId, String content, User currentUser) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", messageId));

        if (!message.getSender().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only edit your own messages");
        }

        message.setContent(content);
        message.setEdited(true);
        return messageRepository.save(message);
    }

    @Transactional
    public Chat createDirectChat(User user1, User user2, String name) {
        Chat chat = new Chat();
        chat.setName(name);
        chat.setDirect(true);
        chat.getParticipants().add(user1);
        chat.getParticipants().add(user2);
        Chat saved = chatRepository.save(chat);
        return saved;
    }
}
