package ProgressoApp.controllers.view;

import ProgressoApp.dto.request.ProjectRequestDTO;
import ProgressoApp.dto.request.TaskRequestDTO;
import ProgressoApp.dto.request.TaskSubmissionRequestDTO;
import ProgressoApp.dto.response.ProjectResponseDTO;
import ProgressoApp.model.Project;
import ProgressoApp.model.Task;
import ProgressoApp.model.TaskStatus;
import ProgressoApp.model.User;
import ProgressoApp.service.ProjectService;
import ProgressoApp.service.TaskService;
import ProgressoApp.service.TaskSubmissionService;
import ProgressoApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tasks")
public class TaskViewController {

  private final TaskService taskService;
  private final ProjectService projectService;
  private final UserService userService;
  private final TaskSubmissionService taskSubmissionService;

  @Autowired
  public TaskViewController(TaskService taskService, ProjectService projectService,
      UserService userService, TaskSubmissionService taskSubmissionService) {
    this.taskService = taskService;
    this.projectService = projectService;
    this.userService = userService;
    this.taskSubmissionService = taskSubmissionService;
  }

  @GetMapping("/details/{id}")
  public String showTaskDetails(@PathVariable Long id, Model model) {
    Task task = taskService.findById(id);
    model.addAttribute("task", task);
    return "task_details";
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
  @PostMapping
  public String createTask(
      @RequestParam String name,
      @RequestParam String description,
      @RequestParam Long projectId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
      @RequestParam(required = false, name = "selectedUsers") List<Long> selectedUsers
  ) {
    TaskRequestDTO dto = new TaskRequestDTO(
        name,
        description,
        0, // lub taskOrder z formularza
        TaskStatus.TO_DO, // lub status z formularza
        projectId,
        dueDate
    );
    Long taskId = taskService.createTask(dto).getTaskId();

    if (selectedUsers != null) {
      taskService.assignUsersToTask(taskId, selectedUsers);
    }

    return "redirect:/tasks";
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
  @PostMapping("/{id}/save")
  public String updateProject(@PathVariable long id, @RequestParam String name,
      @RequestParam String description,
      @RequestParam Long projectId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
      @RequestParam(required = false, name = "selectedUsers") List<Long> selectedUsers) {

    TaskRequestDTO dto = new TaskRequestDTO(
        name,
        description,
        0, // lub taskOrder z formularza
        TaskStatus.TO_DO, // lub status z formularza
        projectId,
        dueDate
    );
    Long taskId = taskService.updateTask(id, dto).getTaskId();

    if (selectedUsers != null) {
      taskService.assignUsersToTask(taskId, selectedUsers);
    }
    return "redirect:/tasks";
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
  @GetMapping("/add")
  public String showCreateForm(Model model) {
    Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
    List<ProjectResponseDTO> projects = projectService.getAllProjects(pageable).getContent();
    model.addAttribute("projects", projects);
    return "task_form";
  }

  /*  @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @GetMapping("/{id}/edit")
    public String editTaskForm(@PathVariable Long id, Model model) {
      Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
      List<ProjectResponseDTO> projects = projectService.getAllProjects(pageable).getContent();
      model.addAttribute("projects", projects);
      Task task = taskService.findById(id);
      model.addAttribute("task", task);
      return "task_edit";
    }*/
  @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
  @GetMapping("/{id}/edit")
  public String editTaskForm(@PathVariable Long id, Model model) {
    Task task = taskService.findById(id);
    Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
    List<ProjectResponseDTO> projects = projectService.getAllProjects(pageable).getContent();

    // Pobierz użytkowników przypisanych do projektu danego zadania:
    Set<User> assignedUsersSet = task.getProject().getUsers();
    List<User> assignedUsers = new ArrayList<>(assignedUsersSet);

    model.addAttribute("projects", projects);
    model.addAttribute("task", task);
    model.addAttribute("assignedUsers", assignedUsers); // <-- dodaj to!

    return "task_edit";
  }


  /*@GetMapping()
  public String showTasks(Model model) {
    List<Task> tasks = taskService.findAll();

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
  }*/

  @GetMapping()
  public String showTasks(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    User user = userService.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found: " + email));

    boolean isAdminOrLecturer = authentication.getAuthorities().stream()
        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") ||
            auth.getAuthority().equals("ROLE_LECTURER"));

    List<Task> tasks;
    if (isAdminOrLecturer) {
      tasks = taskService.findAll(); // wszystkie zadania dla admin/lecturer
    } else {
      tasks = taskService.findAllAssignedToUser(user.getUserId()); // tylko przypisane dla usera
    }

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


  @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
  @GetMapping("/addTasks/{projectId}")
  public String showAddTaskForm(@PathVariable Long projectId, Model model) {
    TaskRequestDTO taskDTO = new TaskRequestDTO(
        "", "", 1, null, projectId, null // status null - ustalisz na backendzie
    );
    model.addAttribute("task", taskDTO);

    Project project = projectService.findById(projectId);

    Set<User> assignedUsersSet = project.getUsers();
    List<User> assignedUsers = new ArrayList<>(assignedUsersSet);

    model.addAttribute("assignedUsers", assignedUsers);
    model.addAttribute("project", project);

    return "task_file"; // nazwa szablonu formularza
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
  @PostMapping("/addTasks/{projectId}")
  public String addTask(
      @PathVariable Long projectId,
      @RequestParam String name,
      @RequestParam String description,
      @RequestParam(required = false) Integer taskOrder,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dueDate,
      @RequestParam(required = false, name = "selectedUsers") List<Long> selectedUsers
  ) {
    TaskRequestDTO taskRequestDTO = new TaskRequestDTO(
        name, description, taskOrder, TaskStatus.TO_DO, projectId, dueDate
    );

    // ZAPISUJEMY TASK i otrzymujemy taskId
    Task createdTask = taskService.createTask(taskRequestDTO);

    // PRZYPISUJEMY użytkowników do zadania (to robi wpisy w task_user)
    if (selectedUsers != null && !selectedUsers.isEmpty()) {
      taskService.assignUsersToTask(createdTask.getTaskId(), selectedUsers);
    }

    return "redirect:/project/details/" + projectId;
  }


  @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
  @PostMapping("/{id}/delete")
  public String deleteTask(@PathVariable Long id, Model model) {
    taskService.deleteTask(id);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    User user = userService.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found: " + email));

    boolean isAdminOrLecturer = authentication.getAuthorities().stream()
        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") ||
            auth.getAuthority().equals("ROLE_LECTURER"));

    List<Task> tasks;
    if (isAdminOrLecturer) {
      tasks = taskService.findAll(); // wszystkie zadania dla admin/lecturer
    } else {
      tasks = taskService.findAllAssignedToUser(user.getUserId()); // tylko przypisane dla usera
    }

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

}
