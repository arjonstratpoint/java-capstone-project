package com.example.arjon.model;

import com.example.arjon.model.request.ContentRequest;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;


/**
 * The Content entity
 *
 */
public record Content(
        @Id
        Integer id,
        Integer userId,
        String title,
        String description,
        Status status,
        Type contentType,
        LocalDateTime dateCreated,
        LocalDateTime dateUpdated,
        String url
) {
        // Used for creating content
        public Content(Integer userId, String title, String desc, Status status, Type contentType, String url) {
                this(null,userId, title, desc, status, contentType, LocalDateTime.now(), null, url);
        }

        // Used for updating content
        public Content(Content content, ContentRequest contentRequest) {
                this(content.id(), content.userId(), contentRequest.title(), contentRequest.desc(), contentRequest.status(), contentRequest.contentType(), content.dateCreated(), LocalDateTime.now(), contentRequest.url());
        }
}
