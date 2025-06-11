package ProgressoApp.controllers;

import ProgressoApp.model.Task;
import ProgressoApp.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

  @PostMapping
  public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
    taskService.createTask(task);
    return new ResponseEntity<>(task, HttpStatus.CREATED);
  }

  @PutMapping("/{taskId}")
  public ResponseEntity<Task> updateTask(@PathVariable Long taskId,
      @Valid @RequestBody Task task) {
    task.setTaskId(taskId); // Ensure the taskId is set correctly before updating
    taskService.updateTask(task);
    return new ResponseEntity<>(task, HttpStatus.OK);
  }

  @DeleteMapping("/{taskId}")
  public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
    taskService.deleteTask(taskId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
  }

  @GetMapping("/{taskId}")
  public ResponseEntity<Task> getTask(@PathVariable Long taskId) {
    // Optional: You can add logic to check if the task exists before returning
    return new ResponseEntity<>(taskService.getTaskById(taskId), HttpStatus.OK);
  }

}
