package ProgressoApp.controllers;

import ProgressoApp.dto.request.TaskRequestDTO;
import ProgressoApp.model.Task;
import ProgressoApp.service.TaskService;
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


}
