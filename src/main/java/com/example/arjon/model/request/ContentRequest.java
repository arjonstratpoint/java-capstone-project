package com.example.arjon.model.request;

import com.example.arjon.model.Status;
import com.example.arjon.model.Type;
import jakarta.validation.constraints.NotBlank;

public record ContentRequest (
        @NotBlank
        String title,
        String desc,
        Status status,
        Type contentType,
        String url
){
}
