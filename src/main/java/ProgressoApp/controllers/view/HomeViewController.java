package ProgressoApp.controllers.view;

import ProgressoApp.config.JwtService;
import ProgressoApp.dto.request.RegisterDTO;
import ProgressoApp.dto.response.ProjectResponseDTO;
import ProgressoApp.repository.ProjectRepository;
import ProgressoApp.repository.TaskRepository;
import ProgressoApp.repository.TaskSubmissionRepository;
import ProgressoApp.repository.UserRepository;
import ProgressoApp.service.ProjectService;
import ProgressoApp.service.TaskService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class HomeViewController {

  private final JwtService jwtService;
  private final ProjectService projectService;
  private final TaskService taskService;
  private final ProjectRepository projectRepository;
  private final TaskRepository taskRepository;
  private final UserRepository userRepository;
  private final TaskSubmissionRepository taskSubmissionRepository;

  @Autowired
  public HomeViewController(JwtService jwtService, ProjectService projectService,
      TaskService taskService, ProjectRepository projectRepository, TaskRepository taskRepository,
      UserRepository userRepository, TaskSubmissionRepository taskSubmissionRepository) {
    this.jwtService = jwtService;
    this.projectService = projectService;
    this.taskService = taskService;
    this.projectRepository = projectRepository;
    this.taskRepository = taskRepository;
    this.userRepository = userRepository;
    this.taskSubmissionRepository = taskSubmissionRepository;
  }


  @GetMapping("/")
  public String home(HttpServletRequest request) {
    if (hasValidJwtToken(request)) {
      return "redirect:/index";
    }
    return "redirect:/login";
  }

  @GetMapping("/index")
  public String showProjectsPage(
      @RequestParam(value = "search", required = false) String search,
      @RequestParam(value = "sort", required = false, defaultValue = "creationTimestamp") String sort,
      @RequestParam(value = "dir", required = false, defaultValue = "desc") String dir,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "5") int size,
      Model model) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();

    boolean isAdminOrLecturer = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(role -> role.equals("ROLE_ADMIN") || role.equals("ROLE_LECTURER"));

    Sort.Direction direction =
        dir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
    Sort sortObj = Sort.by(direction, sort);
    Pageable pageable = PageRequest.of(page, size, sortObj);

    Page<ProjectResponseDTO> projects;

    if (isAdminOrLecturer) {
      if (search != null && !search.isBlank()) {
        projects = projectService.getProjectsByNameContaining(search, pageable, sort, dir);
      } else {
        projects = projectService.getAllProjects(pageable);
      }
    } else {
      if (search != null && !search.isBlank()) {
        projects = projectService.getProjectsByNameContainingForUser(username, search, pageable);
      } else {
        projects = projectService.getProjectsForUser(username, pageable);
      }
    }

    model.addAttribute("projects", projects.getContent());
    model.addAttribute("totalPages", projects.getTotalPages());
    model.addAttribute("currentPage", projects.getNumber());
    model.addAttribute("hasNext", projects.hasNext());
    model.addAttribute("hasPrevious", projects.hasPrevious());

    Map<String, String> params = new HashMap<>();
    params.put("search", search != null ? search : "");
    params.put("sort", sort);
    params.put("dir", dir);
    model.addAttribute("param", params);

    model.addAttribute("currentUserRole", authentication.getAuthorities().toString());

    return "index";
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
