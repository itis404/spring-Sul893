package com.privetmedved.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnoreProperties("profile")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "background_color", length = 7, columnDefinition = "varchar(7) default '#1a1a2e'")
    private String backgroundColor = "#1a1a2e";

    @Column(name = "text_color", length = 7, columnDefinition = "varchar(7) default '#eaeaea'")
    private String textColor = "#eaeaea";

    @Column(name = "accent_color", length = 7, columnDefinition = "varchar(7) default '#6c63ff'")
    private String accentColor = "#6c63ff";

    @Column(name = "font_family", length = 50)
    private String fontFamily;

    @Column(name = "custom_css", columnDefinition = "TEXT")
    private String customCss;

    @Column(name = "background_image_url")
    private String backgroundImageUrl;

    @Column(name = "profile_layout", length = 20, columnDefinition = "varchar(20) default 'default'")
    private String profileLayout = "default";

    public Profile(User user) {
        this.user = user;
        this.displayName = user.getUsername();
    }
}
