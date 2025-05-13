package ProgressoApp.controllers;


import ProgressoApp.auth.Tokens;
import ProgressoApp.config.JwtService;
import ProgressoApp.dto.request.LoginDTO;
import ProgressoApp.dto.request.RegisterDTO;
import ProgressoApp.model.User;
import ProgressoApp.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

  private final UserService userService;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  @Autowired
  public AuthController(@Qualifier("userServiceImpl") UserService userService,
      JwtService jwtService,
      AuthenticationManager authenticationManager) {
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
  public String register(@RequestBody @Valid RegisterDTO user,
      BindingResult result, Model model) {
    Optional<User> existingUser = userService.findByEmail(user.getEmail());

    if (existingUser.isPresent()) {
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


  @PostMapping("/login/add")
  public ResponseEntity<Tokens> login(@RequestBody @Valid LoginDTO loginDTO, BindingResult result,
      Model model) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
    );

    Optional<User> optionalUser = userService.findByEmail(loginDTO.getEmail());

    if (optionalUser.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }

    User user = optionalUser.get();

    String accessToken = jwtService.generateAccessToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);

    return ResponseEntity.ok(new Tokens(accessToken, refreshToken));
  }

  @GetMapping("/logout")
  public String logout(HttpSession session) {
    session.invalidate();
    return "redirect:/";
  }
}
