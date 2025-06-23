package ProgressoApp.controllers.api;

import ProgressoApp.dto.request.ProjectRequestDTO;
import ProgressoApp.dto.response.ProjectResponseDTO;
import ProgressoApp.dto.response.UserResponseDTO;
import ProgressoApp.model.Project;
import ProgressoApp.model.User;
import ProgressoApp.repository.ProjectRepository;
import ProgressoApp.repository.TaskRepository;
import ProgressoApp.repository.UserRepository;
import ProgressoApp.service.ProjectService;
import ProgressoApp.service.UserService;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/project")
public class ProjectController {

  private final ProjectService projectService;
  private final UserService userService;
  @Autowired
  private ProjectRepository projectRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private TaskRepository taskRepository;

  @Autowired
  public ProjectController(ProjectService projectService, UserService userService) {
    this.projectService = projectService;
    this.userService = userService;
  }


  @GetMapping("/all")
  public ResponseEntity<Page<ProjectResponseDTO>> getAllProjects(
      @RequestParam(name = "id", required = false) Long id,
      @RequestParam(name = "name", required = false) String name,
      @RequestParam(name = "description", required = false) String description, Pageable pageable) {

    Map<String, Object> params = new HashMap<>();
    Optional.ofNullable(id).ifPresent(value -> params.put("id", value));
    Optional.ofNullable(name).ifPresent(value -> params.put("name", value));
    Optional.ofNullable(description).ifPresent(value -> params.put("description", value));

    if (params.isEmpty()) {
      return ResponseEntity.ok(projectService.getAllProjects(pageable));
    } else {
      return ResponseEntity.ok(projectService.getAllProjectsWithParams(params, pageable));
    }
  }

  @GetMapping("/users/{id}")
  public ResponseEntity<List<UserResponseDTO>> getProjectUsersById(@PathVariable Long id) {
    Project project = projectService.findProjectById(id);
    Set<User> users = project.getUsers();

    return ResponseEntity.ok(
        users.stream().map(User::toUserResponseDTO).collect(Collectors.toList())
    );
  }


  @PostMapping("/add")
  public String createProject(@ModelAttribute ProjectRequestDTO projectDto) {
    // Check if statusProject is null or empty and assign a default value
    if (projectDto.statusProject() == null || projectDto.statusProject().isEmpty()) {
      // Create a new ProjectRequestDTO with the default statusProject value
      projectDto = new ProjectRequestDTO(
          projectDto.name(),
          projectDto.description(),
          projectDto.creationTimestamp(),
          projectDto.tasks(),
          projectDto.users(),
          projectDto.endDateTime(),
          "active" // Default value
      );
    }
    // Save the project
    projectService.createProject(projectDto);
    return "redirect:/index";  // Redirect after saving the project
  }


  @GetMapping("/add-person/{projectId}")
  public String showAddPeopleToProjectForm(@PathVariable Long projectId, Model model) {
    // Pobieramy projekt na podstawie ID
    Project project = projectRepository.findById(projectId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid project ID"));

    // Pobieramy wszystkich użytkowników, których można przypisać do projektu
    List<User> users = userRepository.findAll();

    // Dodajemy projekt i listę użytkowników do modelu
    model.addAttribute("project", project);
    model.addAttribute("users", users);

    return "add_people_project";  // Widok do przypisywania użytkowników do projektu
  }

}