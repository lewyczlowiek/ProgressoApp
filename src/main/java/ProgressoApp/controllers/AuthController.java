package ProgressoApp.controllers;


import ProgressoApp.auth.AuthenticationResponse;
import ProgressoApp.config.JwtService;
import ProgressoApp.dto.LoginDTO;
import ProgressoApp.dto.RegisterDTO;
import ProgressoApp.model.User;
import ProgressoApp.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  @Autowired
  public AuthController(UserService userService, JwtService jwtService, AuthenticationManager authenticationManager) {
    this.userService = userService;
    this.jwtService = jwtService;
    this.authenticationManager = authenticationManager;
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

    User newUser = userService.findByEmail(user.getEmail());
    String token = jwtService.generateToken(newUser);

    model.addAttribute("token", token);
    return "redirect:/auth/login";
  }

  @GetMapping("/login")
  public String loginPage() {
    return "login";
  }

  @PostMapping("/login")
  public ResponseEntity<AuthenticationResponse> login(@Valid @ModelAttribute("login") LoginDTO loginDTO) {

    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
    );

    User user = userService.findByEmail(loginDTO.getEmail());
    String token = jwtService.generateToken(user);

    AuthenticationResponse authenticationResponse = new AuthenticationResponse(token);
    return ResponseEntity.ok(authenticationResponse);
  }


  @GetMapping("/logout")
  public String logout(HttpSession session) {
    return "redirect:/";
  }
}
