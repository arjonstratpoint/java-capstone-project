package com.example.arjon.controller;

import com.example.arjon.model.Content;
import com.example.arjon.model.Users;
import com.example.arjon.model.request.ContentRequest;
import com.example.arjon.repository.ContentRepository;
import com.example.arjon.repository.UserRepository;
import com.example.arjon.util.AuthenticationFacade;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/content")
public class ContentController {

    private final ContentRepository contentRepository;
    private final AuthenticationFacade authenticationFacade;

    public ContentController(ContentRepository contentRepository, AuthenticationFacade authenticationFacade) {
        this.contentRepository = contentRepository;
        this.authenticationFacade = authenticationFacade;
    }

    @GetMapping
    public List<Content> findAll() {
        return contentRepository.findAll();
    }

    @GetMapping("{id}")
    public Content findById(@PathVariable Integer id) {
        return contentRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Content not found"));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<Content> create(@Valid @RequestBody ContentRequest contentRequest, UriComponentsBuilder ucb) {
        Integer userId = authenticationFacade.getUserIdFromAuthentication();
        Content content = new Content(userId,contentRequest.title(),contentRequest.desc(),contentRequest.status(),contentRequest.contentType(),contentRequest.url());
        Content contentSaved = contentRepository.save(content);
        URI locationOfNewContent = ucb
                .path("/api/content/{id}")
                .buildAndExpand(contentSaved.id())
                .toUri();
        return ResponseEntity.created(locationOfNewContent).body(contentSaved);

    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    public void update(@RequestBody Content content, @PathVariable Integer id) {
        if (!contentRepository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Content not found");
        }

        contentRepository.save(content);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        contentRepository.deleteById(id);
    }

    @GetMapping("/filter/{keyword}")
    public List<Content> findByTitle(@PathVariable  String keyword) {
        return contentRepository.findAllByTitleContains(keyword);
    }
    @GetMapping("/filter/status/{status}")
    public List<Content> findByStatus(@PathVariable  String status) {
        return contentRepository.findByStatus(status);
    }

}
