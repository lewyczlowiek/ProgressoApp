package ProgressoApp.controllers;

import ProgressoApp.dto.request.TaskRequestDTO;
import ProgressoApp.model.*;
import ProgressoApp.repository.ProjectRepository;
import ProgressoApp.repository.TaskRepository;
import ProgressoApp.repository.TaskSubmissionRepository;
import ProgressoApp.repository.UserRepository;
import ProgressoApp.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/tasks")  // Zmieniam na webowy kontroler (thymeleaf), a nie RestController
public class TaskController {

  private final TaskService taskService;
  private final ProjectRepository projectRepository;
  private final TaskRepository taskRepository;
  private final UserRepository userRepository;
  private final TaskSubmissionRepository taskSubmissionRepository;

  @Autowired
  public TaskController(TaskService taskService,
                        ProjectRepository projectRepository,
                        TaskRepository taskRepository,
                        UserRepository userRepository,
                        TaskSubmissionRepository taskSubmissionRepository) {
    this.taskService = taskService;
    this.projectRepository = projectRepository;
    this.taskRepository = taskRepository;
    this.userRepository = userRepository;
    this.taskSubmissionRepository = taskSubmissionRepository;
  }


  @GetMapping("/addTasks/{projectId}")
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

  @PostMapping("/addTasks/{projectId}")
  public String addTask(
          @PathVariable Long projectId,
          @ModelAttribute TaskRequestDTO taskRequestDTO,
          @RequestParam(required = false, name = "selectedUsers") List<Long> selectedUsersIds,
          Model model) {
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
    return "redirect:/api/projects/details/" + projectId;
  }
  @GetMapping
  public String showTasks(Model model) {
    List<Task> tasks = taskRepository.findAll();
    model.addAttribute("tasks", tasks);
    return "task";
  }
  @GetMapping("/{taskId}")
  public String showTaskDetails(@PathVariable Long taskId, Model model) {
    Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Zadanie nie znalezione"));

    List<TaskSubmission> submissions = taskSubmissionRepository.findByTask(task); // jeśli chcesz pliki
    model.addAttribute("task", task);
    model.addAttribute("submissions", submissions);
    return "task_details";
  }
  @GetMapping("/{id}/edit")
  public String editTaskForm(@PathVariable Long id, Model model) {
    Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Zadanie nie znalezione"));
    model.addAttribute("task", task);
    return "task_edit";
  }
  @PostMapping("/{id}/save")
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
