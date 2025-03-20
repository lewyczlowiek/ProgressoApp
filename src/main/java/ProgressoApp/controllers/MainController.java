package ProgressoApp.controllers;

import ProgressoApp.dto.RegisterDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

  @GetMapping("/")
  public String homePage() {
    return "index";
  }

}