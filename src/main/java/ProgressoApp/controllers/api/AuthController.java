package ProgressoApp.controllers.api;


import ProgressoApp.auth.Tokens;
import ProgressoApp.config.JwtService;
import ProgressoApp.dto.request.LoginDTO;
import ProgressoApp.dto.request.RegisterDTO;
import ProgressoApp.model.User;
import ProgressoApp.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
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
  public AuthController(@Qualifier("userService") UserService userService,
      JwtService jwtService,
      AuthenticationManager authenticationManager) {
    this.userService = userService;
    this.jwtService = jwtService;
    this.authenticationManager = authenticationManager;
  }


  @PostMapping("/register/save")
  public ResponseEntity<Map<String, String>> register(@RequestBody @Valid RegisterDTO user,
      BindingResult result) {
    Optional<User> existingUser = userService.findByEmail(user.getEmail());

    if (existingUser.isPresent()) {
      return ResponseEntity
          .badRequest()
          .body(Map.of("message", "EMAIL_IN_USE"));
    }

    if (result.hasErrors()) {
      return ResponseEntity
          .badRequest()
          .body(Map.of("message", "VALIDATION_ERROR"));
    }

    userService.saveUser(user);
    return ResponseEntity.ok(Map.of("message", "SUCCESS"));
  }


  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody @Valid LoginDTO loginDTO, BindingResult result,
      HttpServletResponse response) {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
      );
    } catch (BadCredentialsException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    Optional<User> optionalUser = userService.findByEmail(loginDTO.getEmail());
    if (optionalUser.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }

    User user = optionalUser.get();

    String accessToken = jwtService.generateAccessToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);

    // ustaw ciasteczko HttpOnly z accessToken
    Cookie cookie = new Cookie("jwtToken", accessToken);
    cookie.setHttpOnly(true);
    cookie.setSecure(true);  // jeśli masz HTTPS, ustaw true
    cookie.setPath("/");
    cookie.setMaxAge(60 * 60); // 1 dzień
    response.addCookie(cookie);

    // Możesz też wysłać refresh token w ciele lub w osobnym ciasteczku
    return ResponseEntity.ok(
        new Tokens(null, refreshToken)); // access token nie wysyłamy jawnie bo jest w ciasteczku
  }


  @GetMapping("/logout")
  public String logout(HttpServletResponse response, HttpSession session) {
    session.invalidate();

    Cookie cookie = new Cookie("jwtToken", null);  // nazwa cookie musi być zgodna
    cookie.setHttpOnly(true);
    cookie.setSecure(true); // ustaw jak w loginie (HTTPS true, lokalnie false)
    cookie.setPath("/");
    cookie.setMaxAge(0); // usuń cookie
    response.addCookie(cookie);

    return "redirect:/login";
  }

  @GetMapping("/privacy-policy.html")
  public String privacyPolicy() {
    return "privacy-policy";
  }
}