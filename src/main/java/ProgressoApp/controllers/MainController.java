package ProgressoApp.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

//    @PreAuthorize("isAuthenticated()")
    @GetMapping({"", "/"})
    public String homePage() {
        return "index";
    }
}
