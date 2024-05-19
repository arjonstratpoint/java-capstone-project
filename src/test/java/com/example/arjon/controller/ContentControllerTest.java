package com.example.arjon.controller;

import com.example.arjon.model.Content;
import com.example.arjon.model.Status;
import com.example.arjon.model.Type;
import com.example.arjon.model.request.ContentRequest;
import com.example.arjon.model.request.UserRequest;
import com.example.arjon.repository.ContentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.jfr.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.BDDMockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ContentControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private final String userBaseUrl = "/api/content";
    private final int userId = 2;
    private final int adminId = 1;

    @Test
    @WithMockUser(username = "user", password = "user", authorities = "SCOPE_USER")
    void findAllContentsTest() throws Exception{
        String reponseString = this.mvc.perform(get(userBaseUrl+"/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(userId))
                .andExpect(jsonPath("$[1].userId").value(userId))
                .andExpect(jsonPath("$[2].userId").value(userId))
                .andReturn().getResponse().getContentAsString();
        System.out.println(reponseString);
    }

    @Test
    @WithMockUser(username = "user", password = "user", authorities = "SCOPE_USER")
    void findContentByIDTest() throws Exception{
        String reponseString = this.mvc.perform(get(userBaseUrl+"/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andReturn().getResponse().getContentAsString();
        System.out.println(reponseString);
    }

    @Test
    @Transactional
    @WithMockUser(username = "user", password = "user", authorities = "SCOPE_USER")
    void createContentSuccessful() throws Exception {
        String title = "New Content Title";
        String desc = "New Content Desc";
        ContentRequest request = new ContentRequest(title, desc, Status.PUBLISHED, Type.CONFERENCE_TALK,"");
        this.mvc.perform(post(userBaseUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.description").value(desc))
                .andExpect(jsonPath("$.status").value(Status.PUBLISHED.toString()))
                .andExpect(jsonPath("$.contentType").value(Type.CONFERENCE_TALK.toString()));
    }
}