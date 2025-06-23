package ProgressoApp.controllers.api;

import ProgressoApp.dto.request.TaskRequestDTO;
import ProgressoApp.dto.request.TaskSubmissionRequestDTO;
import ProgressoApp.dto.response.TaskResponseDTO;
import ProgressoApp.model.*;
import ProgressoApp.service.ProjectService;
import ProgressoApp.service.TaskService;
import ProgressoApp.service.TaskSubmissionService;
import ProgressoApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

  private final TaskService taskService;
  private final ProjectService projectService;
  private final UserService userService;
  private final TaskSubmissionService taskSubmissionService;

  @Autowired
  public TaskController(TaskService taskService, ProjectService projectService,
      UserService userService,
      TaskSubmissionService taskSubmissionService) {
    this.taskService = taskService;
    this.projectService = projectService;
    this.userService = userService;
    this.taskSubmissionService = taskSubmissionService;
  }

  @GetMapping
  public Page<TaskResponseDTO> getAllTasks(Pageable pageable) {
    return taskService.getAllTasks(pageable);
  }

  @GetMapping("/search")
  public Page<TaskResponseDTO> getTasksWithParams(
      @RequestParam Map<String, Object> params,
      Pageable pageable
  ) {
    return taskService.getAllTasksWithParams(params, pageable);
  }

  @GetMapping("/{id}")
  public Task getTaskById(@PathVariable Long id) {
    return taskService.findById(id);
  }

  @PutMapping("/{id}")
  public Task updateTask(@PathVariable Long id, @RequestBody TaskRequestDTO dto) {
    return taskService.updateTask(id, dto);
  }

  @PutMapping("/status/{id}")
  public Task updateStatus(@PathVariable Long id, @RequestBody TaskStatus status) {

    return taskService.updateStatus(id, status);
  }

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

  @PostMapping("/{id}/delete")
  public String deleteTask(@PathVariable Long id) {
    taskService.deleteTask(id);
    return "redirect:/task_get";
  }

  @PostMapping("/addTasks/{projectId}")
  public String addTask(
      @PathVariable Long projectId,
      @ModelAttribute TaskRequestDTO taskRequestDTO,
      @RequestParam(required = false, name = "selectedUsers") List<Long> selectedUsersIds
  ) {
    Project project = projectService.findById(projectId);

    // Nadpisujemy DTO poprawnym statusem i projektem
    TaskRequestDTO updatedRequest = new TaskRequestDTO(
        taskRequestDTO.name(),
        taskRequestDTO.description(),
        taskRequestDTO.taskOrder(),
        TaskStatus.TO_DO,
        project.getProjectId(),
        taskRequestDTO.dueDate()
    );

    // ZAPISUJEMY TASK i otrzymujemy taskId
    Task createdTask = taskService.createTask(updatedRequest);

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

  @PatchMapping("/{id}/status")
  public Task updateTaskStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
    String statusString = body.get("taskStatus");
    return taskService.updateTaskStatus(id, statusString);
  }


}


