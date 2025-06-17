package ProgressoApp.controllers;

import ProgressoApp.dto.request.TaskRequestDTO;
import ProgressoApp.model.*;
import ProgressoApp.repository.ProjectRepository;
import ProgressoApp.repository.TaskRepository;
import ProgressoApp.repository.TaskSubmissionRepository;
import ProgressoApp.repository.UserRepository;
import ProgressoApp.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
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

    return "redirect:/api/projects/details/" + projectId;
  }
}
