package ProgressoApp.controllers.view;

import ProgressoApp.dto.response.UserResponseDTO;
import ProgressoApp.model.Role;
import ProgressoApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@Secured("ADMIN")
public class AdminViewController {

  @Autowired
  private final UserService userService;

  public AdminViewController(UserService userService) {
    this.userService = userService;
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
}


