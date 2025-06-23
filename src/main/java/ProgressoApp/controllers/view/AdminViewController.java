package ProgressoApp.controllers.view;

import ProgressoApp.dto.request.UserRequestDTO;
import ProgressoApp.dto.response.UserResponseDTO;
import ProgressoApp.model.Role;
import ProgressoApp.model.TaskSubmission;
import ProgressoApp.repository.TaskSubmissionRepository;
import ProgressoApp.repository.UserRepository;
import ProgressoApp.service.UserService;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ProgressoApp.model.User;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@Secured("ADMIN")
public class AdminViewController {

  @Autowired
  private final UserService userService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final TaskSubmissionRepository submissionRepository;

  public AdminViewController(UserService userService, UserRepository userRepository,
      PasswordEncoder passwordEncoder, TaskSubmissionRepository submissionRepository) {
    this.userService = userService;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.submissionRepository = submissionRepository;
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  @Secured("ADMIN")
  public String usersPage(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size,
      @RequestParam(defaultValue = "email") String sort,
      @RequestParam(defaultValue = "asc") String dir,
      @RequestParam(required = false) String email,
      Model model
  ) {
    Pageable pageable = PageRequest.of(page, size);
    Page<UserResponseDTO> usersPage = userService.getUsersPageFiltered(email, pageable, sort, dir);

    model.addAttribute("usersPage", usersPage);
    model.addAttribute("roles", Role.values());
    model.addAttribute("sort", sort);
    model.addAttribute("dir", dir);
    model.addAttribute("email", email);

    return "admin_view";
  }


  @PostMapping("/edit/{id}")
  public String editUser(@PathVariable Long id, @ModelAttribute User user,
      @RequestParam(required = false) String password) {
    User existing = userService.findById(id);
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
    return "redirect:/admin";
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  @PostMapping("/delete/{id}")
  public String deleteUser(@PathVariable Long id) {
    User user = userRepository.findById(id).orElse(null);
    if (user != null) {
      // Znajdź zgłoszenia użytkownika
      List<TaskSubmission> submissions = submissionRepository.findByUser(user);

      // Odłącz zgłoszenia od użytkownika i zapisz je
      for (TaskSubmission submission : submissions) {
        submission.setUser(null);
        submissionRepository.save(submission);
      }

      // Usuń użytkownika
      userRepository.delete(user);
    }
    return "redirect:/admin";
  }

}


