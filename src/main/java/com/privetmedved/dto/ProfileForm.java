package com.privetmedved.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileForm {

    @Size(max = 100, message = "Display name is too long")
    private String displayName;

    @Size(max = 5000, message = "Bio is too long")
    private String bio;

    @Size(max = 7, message = "Invalid color format")
    private String backgroundColor;

    @Size(max = 7, message = "Invalid color format")
    private String textColor;

    @Size(max = 7, message = "Invalid color format")
    private String accentColor;

    @Size(max = 50, message = "Font family name is too long")
    private String fontFamily;

    @Size(max = 50000, message = "Custom CSS is too long")
    private String customCss;

    private String backgroundImageUrl;

    private String profileLayout;
}
