package com.privetmedved.converter;

import com.privetmedved.dto.UserDto;
import com.privetmedved.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

    public UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());

        if (user.getProfile() != null) {
            dto.setDisplayName(user.getProfile().getDisplayName());
            dto.setAvatarUrl(user.getProfile().getAvatarUrl());
        }

        return dto;
    }
}