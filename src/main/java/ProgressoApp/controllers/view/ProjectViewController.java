package ProgressoApp.controllers.view;

import ProgressoApp.dto.request.ProjectRequestDTO;
import ProgressoApp.model.Project;
import ProgressoApp.model.Task;
import ProgressoApp.model.TaskStatus;
import ProgressoApp.model.User;
import ProgressoApp.repository.ProjectRepository;
import ProgressoApp.repository.UserRepository;
import ProgressoApp.service.ProjectService;
import ProgressoApp.service.TaskService;
import ProgressoApp.service.UserService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectViewController {

  public final ProjectService projectService;
  public final UserService userService;
  public final TaskService taskService;
  @Autowired
  private final ProjectRepository projectRepository;
  @Autowired
  private final UserRepository userRepository;

  @GetMapping("/add")
  public String showAddProjectForm(Model model) {
    ProjectRequestDTO emptyProject = new ProjectRequestDTO(
        "", // Empty project name
        "", // Empty description
        LocalDateTime.now(), // Default current time
        new ArrayList<>(), // Empty task list
        new HashSet<>(), // Empty user set
        LocalDateTime.now(),
        "active"
    );

    model.addAttribute("project", emptyProject);
    return "project_file"; // Return the view for the form
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

  @GetMapping("/details/{projectId}")
  public String showProjectDetails(@PathVariable Long projectId, Model model) {
    Project project = projectService.findById(projectId);

    List<User> users = new ArrayList<>(project.getUsers());

    List<Task> allTasks = project.getTasks();

    List<Task> todoTasks = allTasks.stream()
        .filter(task -> TaskStatus.TO_DO.equals(task.getTaskStatus()))
        .collect(Collectors.toList());

    List<Task> inProgressTasks = allTasks.stream()
        .filter(task -> TaskStatus.IN_PROGRESS.equals(task.getTaskStatus()))
        .collect(Collectors.toList());

    List<Task> doneTasks = allTasks.stream()
        .filter(task -> TaskStatus.DONE.equals(task.getTaskStatus()))
        .collect(Collectors.toList());

    model.addAttribute("project", project);
    model.addAttribute("users", users);
    model.addAttribute("todoTasks", todoTasks);
    model.addAttribute("inProgressTasks", inProgressTasks);
    model.addAttribute("doneTasks", doneTasks);

    return "details_project";
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
  @GetMapping("/add-person/{projectId}")
  public String showAddPeopleToProjectForm(@PathVariable Long projectId, Model model) {
    Project project = projectService.findById(projectId);
    List<User> users = userService.findAll();

    model.addAttribute("project", project);
    model.addAttribute("users", users);

    return "add_people_project";
  }


  @PostMapping("/{id}/edit")
  public String updateProject(@PathVariable long id, @ModelAttribute ProjectRequestDTO projectDto) {

    projectService.updateProject(id, projectDto);
    return "redirect:/index";
  }


  @GetMapping("/edit/{id}")
  public String showEditProjectForm(@PathVariable long id, Model model) {
    // Pobieramy projekt
    Project project = projectService.findById(id);
    if (project == null) {
      return "redirect:/index";  // Jeśli projekt nie istnieje, przekierowanie na stronę główną
    }

    // Formatowanie endDateTime na 'yyyy-MM-dd'T'HH:mm'
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    String formattedEndDateTime = project.getEndDateTime().format(formatter);

    // Dodanie projektu do modelu
    model.addAttribute("project", project);
    model.addAttribute("formattedEndDateTime", formattedEndDateTime);

    return "edit_project";  // Przekazanie modelu do widoku
  }

  @GetMapping("/delete/{id}")
  public String deleteProject(@PathVariable long id) {
    projectService.deleteProject(id);  // Wywołanie metody usuwania
    return "redirect:/index";   // Przekierowanie po usunięciu projektu
  }

  @PostMapping("/add-person/{projectId}")
  public String addUsersToProject(@PathVariable Long projectId,
      @RequestParam(required = false) Set<Long> selectedUsers) {
    // Logowanie dla diagnostyki
    System.out.println("Próba dodania użytkowników do projektu o ID: " + projectId);
    System.out.println("Wybrani użytkownicy (ID): " + selectedUsers);

    // Pobranie projektu na podstawie ID
    Project project = projectRepository.findById(projectId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid project ID"));

    // Jeśli nie wybrano żadnych użytkowników, przekierowujemy na stronę /index
    if (selectedUsers == null || selectedUsers.isEmpty()) {
      return "redirect:/index";  // Przekierowanie na stronę główną, jeśli żaden użytkownik nie został wybrany
    }

    // Pobranie użytkowników, którzy są aktualnie przypisani do projektu
    Set<User> currentUsers = project.getUsers();

    // Pobranie użytkowników na podstawie ich ID
    Set<User> usersToAdd = new HashSet<>(userRepository.findAllById(selectedUsers));

    // Użytkownicy do usunięcia (odznaczeni w formularzu)
    Set<User> usersToRemove = new HashSet<>(currentUsers);
    usersToRemove.removeAll(usersToAdd); // Usuwamy tych, którzy są teraz zaznaczeni

    // Użytkownicy do dodania (zaznaczeni w formularzu, ale nie w projekcie)
    usersToAdd.removeAll(currentUsers); // Usuwamy tych, którzy są już przypisani

    // Przypisanie nowych użytkowników do projektu
    currentUsers.addAll(usersToAdd);
    currentUsers.removeAll(usersToRemove); // Usuwamy użytkowników, którzy zostali odznaczeni

    // Zapisanie zmian
    projectRepository.save(project);

    // Po zapisaniu zmian przekierowujemy na stronę z projektem lub listą projektów
    return "redirect:/index";  // Możesz dostosować tę ścieżkę, aby przekierować użytkownika na odpowiednią stronę
  }

}
