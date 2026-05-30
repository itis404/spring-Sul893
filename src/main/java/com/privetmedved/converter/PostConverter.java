package com.privetmedved.converter;

import com.privetmedved.dto.PostDto;
import com.privetmedved.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class PostConverter {

    public PostDto toDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setVoteCount(post.getVoteCount());
        dto.setPinned(post.isPinned());
        dto.setCreatedAt(post.getCreatedAt());

        if (post.getAuthor() != null) {
            dto.setAuthorUsername(post.getAuthor().getUsername());
            if (post.getAuthor().getProfile() != null) {
                dto.setAuthorDisplayName(post.getAuthor().getProfile().getDisplayName());
            }
        }

        if (post.getRoom() != null) {
            dto.setRoomName(post.getRoom().getName());
            dto.setRoomSlug(post.getRoom().getSlug());
        }

        dto.setCommentCount(post.getComments() != null ? post.getComments().size() : 0);

        if (post.getTags() != null) {
            post.getTags().forEach(tag -> dto.getTags().add(tag.getName()));
        }

        return dto;
    }
}