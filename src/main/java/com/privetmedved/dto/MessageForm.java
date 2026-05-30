package com.privetmedved.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MessageForm {

    @Size(min = 1, max = 5000, message = "Message must be between 1 and 5000 characters")
    private String content;
}
