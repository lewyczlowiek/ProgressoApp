package ProgressoApp.controllers;

import ProgressoApp.dto.request.TaskRequestDTO;
import ProgressoApp.repository.ProjectRepository;
import ProgressoApp.repository.TaskRepository;
import ProgressoApp.repository.TaskSubmissionRepository;
import ProgressoApp.repository.UserRepository;
import ProgressoApp.service.ProjectService;
import ProgressoApp.dto.response.ProjectResponseDTO;
import ProgressoApp.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ProgressoApp.model.*;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class MainController {

  private final ProjectService projectService;
  private final TaskService taskService;
  private final ProjectRepository projectRepository;
  private final TaskRepository taskRepository;
  private final UserRepository userRepository;
  private final TaskSubmissionRepository taskSubmissionRepository;
  @Autowired
  public MainController(ProjectService projectService, TaskService taskService, ProjectRepository projectRepository, TaskRepository taskRepository, UserRepository userRepository, TaskSubmissionRepository taskSubmissionRepository) {
    this.projectService = projectService;
      this.taskService = taskService;
      this.projectRepository = projectRepository;
      this.taskRepository = taskRepository;
      this.userRepository = userRepository;
      this.taskSubmissionRepository = taskSubmissionRepository;
  }


  @GetMapping("/index")
  public String showProjectsPage(
      @RequestParam(value = "search", required = false) String search,
      @RequestParam(value = "sort", required = false, defaultValue = "creationTimestamp") String sort,
      @RequestParam(value = "dir", required = false, defaultValue = "desc") String dir,
      Model model, Pageable pageable) {

    // Pobieramy informacje o użytkowniku (rola)
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserRole = authentication.getAuthorities()
        .toString();  // Pobieramy role użytkownika

    // Pobieramy projekty na podstawie parametrów
    Page<ProjectResponseDTO> projects;
    if (search != null && !search.isBlank()) {
      projects = projectService.getProjectsByNameContaining(search, pageable, sort, dir);
    } else {
      projects = projectService.getAllProjects(pageable, sort, dir);
    }

    model.addAttribute("projects", projects.getContent());

    // Parametry do formularza
    Map<String, String> params = new HashMap<>();
    params.put("search", search != null ? search : "");
    params.put("sort", sort);
    params.put("dir", dir);
    model.addAttribute("param", params);

    // Dodajemy rolę użytkownika do modelu
    model.addAttribute("currentUserRole", currentUserRole);

    return "index";  // Zwracamy widok 'index'
  }

  @GetMapping("/tasks/addTasks/{projectId}")
  public String showAddTaskForm(@PathVariable Long projectId, Model model) {
    TaskRequestDTO taskDTO = new TaskRequestDTO(
            "", "", 1, null, projectId, null // status null - ustalisz na backendzie
    );
    model.addAttribute("task", taskDTO);

    Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Projekt nie znaleziony"));

    // Pobierz użytkowników przypisanych do projektu
    Set<User> assignedUsersSet = project.getUsers();
    List<User> assignedUsers = new ArrayList<>(assignedUsersSet);

    model.addAttribute("assignedUsers", assignedUsers);
    model.addAttribute("project", project);

    return "task_file"; // nazwa szablonu formularza
  }

  @PostMapping("/tasks/addTasks/{projectId}")
  public String addTask(
          @PathVariable Long projectId,
          @ModelAttribute TaskRequestDTO taskRequestDTO,
          @RequestParam(required = false, name = "selectedUsers") List<Long> selectedUsersIds
  ) {
    Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Projekt nie znaleziony"));

    Task task = new Task(taskRequestDTO);
    task.setProject(project);
    task.setTaskStatus(TaskStatus.TO_DO);

    taskRepository.save(task);

    if (selectedUsersIds != null && !selectedUsersIds.isEmpty()) {
      for (Long userId : selectedUsersIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony: " + userId));
        // Tworzymy TaskSubmission - oznacza, że użytkownik jest przypisany do zadania
        TaskSubmission submission = new TaskSubmission();
        submission.setTask(task);
        submission.setUser(user);
        // Możesz ustawić inne pola TaskSubmission, np. submittedAt na null jeśli jeszcze nie było zgłoszenia
        taskSubmissionRepository.save(submission);
      }
    }

    return "redirect:/api/project/details/" + projectId;
  }

  @GetMapping("/tasks")
  public String showTasks(Model model) {
    List<Task> tasks = taskRepository.findAll();

    // Formatowanie daty do Stringa
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Mapowanie zadań z dodaną sformatowaną datą
    List<Map<String, Object>> taskList = tasks.stream().map(task -> {
      Map<String, Object> taskMap = new HashMap<>();
      taskMap.put("taskId", task.getTaskId());
      taskMap.put("name", task.getName());
      taskMap.put("taskStatus", task.getTaskStatus());
      taskMap.put("formattedCreationDate", task.getCreationTimestamp().format(formatter));
      return taskMap;
    }).collect(Collectors.toList());

    model.addAttribute("tasks", taskList);
    return "task_get";
  }
  @GetMapping("/tasks/{taskId}")
  public String showTaskDetails(@PathVariable Long taskId, Model model) {
    Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Zadanie nie znalezione"));

    List<TaskSubmission> submissions = taskSubmissionRepository.findByTask(task); // jeśli chcesz pliki
    model.addAttribute("task", task);
    model.addAttribute("submissions", submissions);
    return "task_details";
  }
  @GetMapping("/tasks/{id}/edit")
  public String editTaskForm(@PathVariable Long id, Model model) {
    Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Zadanie nie znalezione"));
    model.addAttribute("task", task);
    return "task_edit";
  }
  @PostMapping("/tasks/{id}/save")
  public String saveTask(@PathVariable Long id,
                         @RequestParam String name,
                         @RequestParam String description,
                         @RequestParam TaskStatus taskStatus,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate) {
    Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Zadanie nie znalezione"));

    task.setName(name);
    task.setDescription(description);
    task.setTaskStatus(taskStatus);
    task.setDueDate(dueDate);
    taskRepository.save(task);

    return "redirect:/tasks/" + id;
  }
}
