package com.example.arjon.controller;

import com.example.arjon.model.Content;
import com.example.arjon.model.request.ContentRequest;
import com.example.arjon.repository.ContentRepository;
import com.example.arjon.util.AuthenticationFacade;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.List;
import java.util.Optional;
/**
 * REST API for contents
 *
 * All content endpoints requires authentication as defined in SecurityConfig of the app
 * except the DELETE method which only permits admin users, annotated by @PreAuthorize
 */
@RestController
@RequestMapping("/api/content")
public class ContentController {

    private final ContentRepository contentRepository;
    private final AuthenticationFacade authenticationFacade;

    public ContentController(ContentRepository contentRepository, AuthenticationFacade authenticationFacade) {
        this.contentRepository = contentRepository;
        this.authenticationFacade = authenticationFacade;
    }

    // Gets all the content list of the authenticated user
    @GetMapping("/list")
    public List<Content> findAll() {
        Integer userId = authenticationFacade.getUserIdFromAuthentication();
        return contentRepository.findAll(userId);
    }

    // Gets a specific content for the authenticated user
    @GetMapping("{id}")
    public Content findById(@PathVariable Integer id) {
        Integer userId = authenticationFacade.getUserIdFromAuthentication();
        return contentRepository.findById(userId, id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Content not found"));
    }

    // Creates a new content for the authenticated user
    @PostMapping
    public ResponseEntity<Content> create(@Valid @RequestBody ContentRequest contentRequest) {
        Integer userId = authenticationFacade.getUserIdFromAuthentication();
        Content content = new Content(userId,contentRequest.title(),contentRequest.desc(),contentRequest.status(),contentRequest.contentType(),contentRequest.url());
        Content contentSaved = contentRepository.save(content);
        return entityWithLocation(contentSaved);
    }

    // Updates a specific content for the authenticated user
    @PutMapping("/{id}")
    public ResponseEntity<Content> update(@Valid @RequestBody ContentRequest contentRequest, @PathVariable Integer id) {
        Integer userId = authenticationFacade.getUserIdFromAuthentication();
        Optional<Content> optionalContent = contentRepository.findById(userId, id);
        if (optionalContent.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Content not found");
        }
        Content content = new Content(optionalContent.get(), contentRequest);
        contentRepository.save(content);
        return entityWithLocation(content);
    }

    // Deletes a content permanently
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        contentRepository.deleteById(id);
    }

    // Gets all the content list with the given keyword owned by the authenticated user
    @GetMapping("/filter/{keyword}")
    public List<Content> findByTitle(@PathVariable  String keyword) {
        Integer userId = authenticationFacade.getUserIdFromAuthentication();
        return contentRepository.findAllByTitleContains(userId, "%"+keyword+"%");
    }

    // Gets all the content list with the given status owned by the authenticated user
    @GetMapping("/filter/status/{status}")
    public List<Content> findByStatus(@PathVariable  String status) {
        Integer userId = authenticationFacade.getUserIdFromAuthentication();
        return contentRepository.findByStatus(userId, status);
    }

    private ResponseEntity<Content> entityWithLocation(Content content) {
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/api/content/{id}")
                .buildAndExpand(content.id())
                .toUri();
        return ResponseEntity.created(uri).body(content);
    }

}
