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

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Task createTask(@RequestBody TaskRequestDTO dto) {
    return taskService.createTask(dto);
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

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTask(@PathVariable Long id) {
    taskService.deleteTask(id);
  }

  @PostMapping("/tasks/addTasks/{projectId}")
  public String addTask(
      @PathVariable Long projectId,
      @ModelAttribute TaskRequestDTO taskRequestDTO,
      @RequestParam(required = false, name = "selectedUsers") List<Long> selectedUsersIds
  ) {
    Project project = projectService.findById(projectId);

    Task task = new Task(taskRequestDTO);
    task.setProject(project);
    task.setTaskStatus(TaskStatus.TO_DO);
    TaskRequestDTO taskRequest = new TaskRequestDTO(task.getName(), task.getDescription(),
        task.getTaskOrder(), task.getTaskStatus(), project.getProjectId(), task.getDueDate());

    taskService.createTask(taskRequest);

    if (selectedUsersIds != null && !selectedUsersIds.isEmpty()) {
      for (Long userId : selectedUsersIds) {
        User user = userService.findById(userId);

        TaskSubmissionRequestDTO submission = new TaskSubmissionRequestDTO(
            task.getTaskId(),
            user.getUserId(),
            "",
            null,
            "",
            null);

        taskSubmissionService.createTaskSubmission(submission);
      }
    }

    return "redirect:/api/project/details/" + projectId;
  }
//  @PostMapping("/tasks/{id}/save")
//  public String saveTask(@PathVariable Long id,
//                         @RequestParam String name,
//                         @RequestParam String description,
//                         @RequestParam TaskStatus taskStatus,
//                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate) {
//    Task task = taskService.findById(id);
//    task.setName(name);
//    task.setDescription(description);
//    task.setTaskStatus(taskStatus);
//    task.setDueDate(dueDate);
//    taskService.createTask(task);
//
//    return "redirect:/tasks/" + id;
//  }
}


