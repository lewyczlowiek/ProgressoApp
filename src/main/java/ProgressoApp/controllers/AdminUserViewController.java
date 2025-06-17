package ProgressoApp.controllers;

import ProgressoApp.model.Role;
import ProgressoApp.model.User;
import ProgressoApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserViewController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;  // <-- to dodaj


    @GetMapping
    public String usersPage(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("roles", Role.values());
        return "admin_view";
    }

    @PostMapping("/edit/{id}")
    public String editUser(@PathVariable Long id, @ModelAttribute User user, @RequestParam(required = false) String password) {
        User existing = userRepository.findById(id).orElseThrow();
        existing.setFirstName(user.getFirstName());
        existing.setLastName(user.getLastName());
        existing.setEmail(user.getEmail());
        existing.setNumberIndex(user.getNumberIndex());
        existing.setRole(user.getRole());
        if (password != null && !password.isBlank()) {
            // zakładam, że masz wstrzyknięty PasswordEncoder
            existing.setPassword(passwordEncoder.encode(password));
        }
        userRepository.save(existing);
        return "redirect:/admin/users";
    }


    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }
}
