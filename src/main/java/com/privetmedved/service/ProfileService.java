package com.privetmedved.service;

import com.privetmedved.converter.ProfileConverter;
import com.privetmedved.dto.ProfileDto;
import com.privetmedved.dto.ProfileForm;
import com.privetmedved.entity.Profile;
import com.privetmedved.exception.ResourceNotFoundException;
import com.privetmedved.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileConverter profileConverter;

    @Transactional(readOnly = true)
    @Cacheable(value = "profiles", key = "#userId")
    public ProfileDto findByUserId(Long userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user", userId));
        return profileConverter.toDto(profile);
    }

    @Transactional(readOnly = true)
    public Profile getProfileEntity(Long userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user", userId));
    }

    @Transactional
    @CacheEvict(value = "profiles", key = "#userId")
    public Profile updateProfile(Long userId, ProfileForm form) {
        Profile profile = getProfileEntity(userId);

        if (form.getDisplayName() != null) profile.setDisplayName(form.getDisplayName());
        if (form.getBio() != null) profile.setBio(form.getBio());
        if (form.getBackgroundColor() != null) profile.setBackgroundColor(form.getBackgroundColor());
        if (form.getTextColor() != null) profile.setTextColor(form.getTextColor());
        if (form.getAccentColor() != null) profile.setAccentColor(form.getAccentColor());
        if (form.getFontFamily() != null) profile.setFontFamily(form.getFontFamily());
        if (form.getCustomCss() != null) profile.setCustomCss(form.getCustomCss());
        if (form.getBackgroundImageUrl() != null) profile.setBackgroundImageUrl(form.getBackgroundImageUrl());
        if (form.getProfileLayout() != null) profile.setProfileLayout(form.getProfileLayout());

        Profile saved = profileRepository.save(profile);
        return saved;
    }
}
