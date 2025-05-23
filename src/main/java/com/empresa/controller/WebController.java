package com.empresa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/institutions")
    public String institutionsList() {
        return "institutions";
    }

    @GetMapping("/request.jsp")
    public String requestJsp() {
        // Redirect old JSP URLs to new endpoints
        return "redirect:/";
    }

    @GetMapping("/listrequest.jsp")
    public String listRequestJsp() {
        // Redirect old JSP URLs to new endpoints
        return "redirect:/institutions";
    }

    @GetMapping("/index")
    public String indexAction() {
        // Support for old Struts action URLs
        return "redirect:/";
    }
}