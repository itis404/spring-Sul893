package com.privetmedved.converter;

import com.privetmedved.dto.ProfileDto;
import com.privetmedved.entity.Profile;
import org.springframework.stereotype.Component;

@Component
public class ProfileConverter {
    public ProfileDto toDto(Profile profile) {
        if (profile == null) return null;
        ProfileDto dto = new ProfileDto();
        dto.setId(profile.getId());
        dto.setDisplayName(profile.getDisplayName());
        dto.setBio(profile.getBio());
        dto.setAvatarUrl(profile.getAvatarUrl());
        dto.setBackgroundColor(profile.getBackgroundColor());
        dto.setTextColor(profile.getTextColor());
        dto.setAccentColor(profile.getAccentColor());
        dto.setFontFamily(profile.getFontFamily());
        dto.setCustomCss(profile.getCustomCss());
        dto.setBackgroundImageUrl(profile.getBackgroundImageUrl());
        dto.setProfileLayout(profile.getProfileLayout());
        return dto;
    }
}