package ProgressoApp.controllers;


import ProgressoApp.dto.RegisterDTO;
import ProgressoApp.model.User;
import ProgressoApp.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

  private final UserService userService;

  @Autowired
  public AuthController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/register")
  public String showRegisterForm(Model model) {
    RegisterDTO user = new RegisterDTO();
    model.addAttribute("user", user);
    return "register";
  }

  @PostMapping("/register/save")
  public String register(@Valid @ModelAttribute("user") RegisterDTO user,
      BindingResult result, Model model) {
    User existingUser = userService.findByEmail(user.getEmail());
    if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail()
        .isEmpty()) {
      result.rejectValue("email", null, "Ten adres email jest niedostępny");
    }
    if (result.hasErrors()) {
      model.addAttribute("user", user);
      return "register";
    }
    userService.saveUser(user);
    return "redirect:/auth/register?success";
  }

  @GetMapping("/login")
  public String loginPage() {
    return "login";
  }

  @GetMapping("/logout")
  public String logout(HttpSession session) {
    return "redirect:/";
  }
}
