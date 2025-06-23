package ProgressoApp.controllers;

import ProgressoApp.dto.request.TaskRequestDTO;
import ProgressoApp.dto.response.TaskResponseDTO;
import ProgressoApp.model.Task;
import ProgressoApp.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

  private final TaskService taskService;

  @Autowired
  public TaskController(TaskService taskService) {
    this.taskService = taskService;
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

  @PostMapping("/{id}")
  public Task updateTask(@PathVariable Long id, @ModelAttribute TaskRequestDTO dto) {
    return taskService.updateTask(id, dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTask(@PathVariable Long id) {
    taskService.deleteTask(id);
  }

  @PatchMapping("/{id}/status")
  public Task updateTaskStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
    String statusString = body.get("taskStatus");
    return taskService.updateTaskStatus(id, statusString);
  }


}


