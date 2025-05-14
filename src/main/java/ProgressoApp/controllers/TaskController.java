package ProgressoApp.controllers;

import ProgressoApp.dto.request.TaskRequestDTO;
import ProgressoApp.model.Task;
import ProgressoApp.service.TaskService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/tasks")
public class TaskController {

  private final TaskService taskService;

  @Autowired
  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

//  @GetMapping
//  public ResponseEntity<Task> getAllTasks() {
//    return taskService.getAllTasks();
//  }
//
//  @GetMapping("/project/{id}")
//  public List<Task> getAllTasksForProject(@PathVariable Long id) {
//    return taskService.getAllTasksForProject(id);
//  }

  @GetMapping("/{id}")
  public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
    return taskService.getTaskById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public Task createTask(@RequestBody TaskRequestDTO dto) {
    return taskService.createTask(dto);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
    try {
      return ResponseEntity.ok(taskService.updateTask(id, task));
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
    taskService.deleteTask(id);
    return ResponseEntity.noContent().build();
  }
}
