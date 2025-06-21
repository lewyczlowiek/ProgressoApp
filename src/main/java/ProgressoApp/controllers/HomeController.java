package ProgressoApp.controllers;

import ProgressoApp.config.JwtService;
import ProgressoApp.dto.request.RegisterDTO;
import ProgressoApp.model.User;
import ProgressoApp.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

  private final JwtService jwtService;

  @Autowired
  public HomeController(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @GetMapping("/")
  public String home(HttpServletRequest request) {
    if (hasValidJwtToken(request)) {
      return "redirect:/index";
    }
    return "redirect:/login";
  }


  @GetMapping("/login")
  public String loginPage(HttpServletRequest request) {
    if (hasValidJwtToken(request)) {
      return "redirect:/index";
    }
    return "login";
  }

  @GetMapping("/register")
  public String showRegisterForm(Model model) {
    RegisterDTO user = new RegisterDTO();
    model.addAttribute("user", user);
    return "register";
  }

  private boolean hasValidJwtToken(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      return false;
    }

    for (Cookie cookie : cookies) {
      if ("jwtToken".equals(cookie.getName())) {
        String token = cookie.getValue();
        return jwtService.isTokenValid(token);
      }
    }
    return false;
  }


}
