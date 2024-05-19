package com.example.arjon.model;

import com.example.arjon.model.request.ContentRequest;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

public record Content(
        @Id
        Integer id,
        Integer userId,
        String title,
        @Column(value = "description")
        String desc,
        Status status,
        Type contentType,
        LocalDateTime dateCreated,
        LocalDateTime dateUpdated,
        String url
) {
        public Content(Integer userId, String title, String desc, Status status, Type contentType, String url) {
                this(null,userId, title, desc, status, contentType, LocalDateTime.now(), null, url);
        }

        public Content(Content content, ContentRequest contentRequest) {
                this(content.id(), content.userId(), contentRequest.title(), contentRequest.desc(), contentRequest.status(), contentRequest.contentType(), content.dateCreated(), LocalDateTime.now(), contentRequest.url());
        }
}
