package com.empresa.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WebController.class)
class WebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testIndexPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void testInstitutionsPage() throws Exception {
        mockMvc.perform(get("/institutions"))
                .andExpect(status().isOk())
                .andExpect(view().name("institutions"));
    }

    @Test
    void testRequestJspRedirect() throws Exception {
        mockMvc.perform(get("/request.jsp"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void testListRequestJspRedirect() throws Exception {
        mockMvc.perform(get("/listrequest.jsp"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/institutions"));
    }

    @Test
    void testIndexActionRedirect() throws Exception {
        mockMvc.perform(get("/index"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}