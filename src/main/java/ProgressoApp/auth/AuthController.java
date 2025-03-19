package ProgressoApp.auth;


import ProgressoApp.dto.RegisterRequest;
import ProgressoApp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {


    @Autowired
    private AuthenticationService authenticationService;
    private UserService userService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register"; // Strona rejestracji
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest request) {
        userService.registerUser(request);  // Rejestracja użytkownika
        return "redirect:/login";  // Przekierowanie do strony logowania
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("authenticationRequest", new AuthenticationRequest());
        return "login";
    }

    @PostMapping
    public String login(@ModelAttribute AuthenticationRequest authenticationRequest, Model model) {
        try {
            AuthenticationResponse response = authenticationService.authenticate(authenticationRequest);
            return "redirect:/index";
        } catch (BadCredentialsException e) {
            model.addAttribute("error", "Błędne dane logowania!");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout() {
        return "login";
    }

}
