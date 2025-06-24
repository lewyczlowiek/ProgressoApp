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
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
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

  @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
  @GetMapping("/{id}/edit")
  public String editTaskForm(@PathVariable Long id, Model model) {
    Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
    List<ProjectResponseDTO> projects = projectService.getAllProjects(pageable).getContent();
    model.addAttribute("projects", projects);
    Task task = taskService.findById(id);
    model.addAttribute("task", task);
    return "task_edit";
  }

  @GetMapping()
  public String showTasks(Model model, Principal principal) {
    // Pobierz aktualnie zalogowanego użytkownika po emailu (username)
    User user = userService.findByEmail(principal.getName()).orElseThrow();

    // Pobierz wszystkie zadania, ale tylko z projektów, do których należy użytkownik
    List<Task> allTasks = taskService.findAll();
    List<Task> userTasks = allTasks.stream()
        .filter(task -> task.getProject() != null &&
            task.getProject().getUsers().contains(user))
        .collect(Collectors.toList());

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    List<Map<String, Object>> taskList = userTasks.stream().map(task -> {
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
      @RequestParam(required = false, name = "selectedUsersIds") List<Long> selectedUsersIds
  ) {
    TaskRequestDTO taskRequestDTO = new TaskRequestDTO(name, description, taskOrder,
        TaskStatus.TO_DO, projectId, dueDate);

    // ZAPISUJEMY TASK i otrzymujemy taskId
    Task createdTask = taskService.createTask(taskRequestDTO);

    // Teraz createdTask.getTaskId() nie jest null
    if (selectedUsersIds != null && !selectedUsersIds.isEmpty()) {
      for (Long userId : selectedUsersIds) {
        User user = userService.findById(userId);

        TaskSubmissionRequestDTO submission = new TaskSubmissionRequestDTO(
            createdTask.getTaskId(), // teraz to działa!
            user.getUserId(),
            "",
            null,
            "",
            null
        );

        taskSubmissionService.createTaskSubmission(submission);
      }
    }

    return "redirect:/project/details/" + projectId;
  }


  @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
  @PostMapping("/{id}/delete")
  public String deleteTask(@PathVariable Long id) {
    taskService.deleteTask(id);
    return "/task_get";
  }

}
