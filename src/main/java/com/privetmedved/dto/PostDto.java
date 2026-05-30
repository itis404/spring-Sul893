package com.privetmedved.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class PostDto {

    private Long id;
    private String title;
    private String content;
    private String authorUsername;
    private String authorDisplayName;
    private String roomName;
    private String roomSlug;
    private int voteCount;
    private boolean pinned;
    private long commentCount;
    private LocalDateTime createdAt;
    private Set<String> tags = new HashSet<>();
}
