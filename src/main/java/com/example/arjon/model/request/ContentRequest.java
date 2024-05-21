package com.example.arjon.model.request;

import com.example.arjon.model.Status;
import com.example.arjon.model.Type;
import jakarta.validation.constraints.NotBlank;

/**
 * A request object for creating and updating a content
 */
public record ContentRequest (
        @NotBlank
        String title,
        String desc,
        Status status,
        Type contentType,
        String url
){
}
