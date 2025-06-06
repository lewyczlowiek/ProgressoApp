package ProgressoApp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

  @GetMapping("/")
  public String homePage() {
    return "index";
  }

  @GetMapping("/login")
  public String loginPage() {
    return "login";
  }

/*    @GetMapping("/chat")
    public String chatView() {
      return "chat";
    }*/

}