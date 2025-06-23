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


  @PatchMapping("/{id}/status")
  public Task updateTaskStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
    String statusString = body.get("taskStatus");
    return taskService.updateTaskStatus(id, statusString);
  }


}


