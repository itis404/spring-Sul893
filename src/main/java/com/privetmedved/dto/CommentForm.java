package com.privetmedved.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentForm {

    @NotBlank(message = "Comment is required")
    @Size(min = 1, max = 10000, message = "Comment must be between 1 and 10000 characters")
    private String content;

    private Long parentId;
}
