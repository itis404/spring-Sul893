package com.privetmedved.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChatForm {

    @NotBlank(message = "Username is required")
    private String username;

    @Size(max = 100, message = "Chat name must be up to 100 characters")
    private String name;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}