package com.privetmedved.dto;

import lombok.Data;

@Data
public class ProfileDto {
    private Long id;
    private String displayName;
    private String bio;
    private String avatarUrl;
    private String backgroundColor;
    private String textColor;
    private String accentColor;
    private String fontFamily;
    private String customCss;
    private String backgroundImageUrl;
    private String profileLayout;
}
