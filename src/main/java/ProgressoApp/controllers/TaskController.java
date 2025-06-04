package ProgressoApp.controllers;

import ProgressoApp.dto.request.TaskRequestDTO;
import ProgressoApp.dto.response.TaskResponseDTO;
import ProgressoApp.model.TaskStatus;
import ProgressoApp.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
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
  public ResponseEntity<TaskResponseDTO> createTask(
          @Valid @RequestBody TaskRequestDTO dto
  ) {
    var created = taskService.createTask(dto);
    return new ResponseEntity<>(created.toTaskResponseDTO(), HttpStatus.CREATED);
  }


  @GetMapping
  public Page<TaskResponseDTO> getAllTasks(
          @RequestParam(required = false) Map<String, String> allRequestParams,
          Pageable pageable
  ) {
    if (allRequestParams.isEmpty()) {
      return taskService.getAllTasks(pageable);
    }

    // Konwersja String → właściwy typ (TaskStatus, LocalDate, Long, Integer, lub String)
    Map<String, Object> filter = new HashMap<>();
    for (var entry : allRequestParams.entrySet()) {
      String key = entry.getKey();
      String raw = entry.getValue();

      switch (key) {
        case "taskStatus":
          try {
            filter.put(key, TaskStatus.valueOf(raw));
          } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nieprawidłowa wartość dla taskStatus: " + raw
            );
          }
          break;

        case "dueDate":
          // Format: yyyy-MM-dd
          try {
            filter.put(key, LocalDate.parse(raw));
          } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nieprawidłowy format dueDate (wymagany yyyy-MM-dd): " + raw
            );
          }
          break;

        case "projectId":
          try {
            filter.put(key, Long.valueOf(raw));
          } catch (NumberFormatException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nieprawidłowy format projectId (wymagany Long): " + raw
            );
          }
          break;

        case "taskOrder":
          try {
            filter.put(key, Integer.valueOf(raw));
          } catch (NumberFormatException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nieprawidłowy format taskOrder (wymagany Integer): " + raw
            );
          }
          break;

        case "name":
          filter.put(key, raw);
          break;

        default:
          throw new ResponseStatusException(
                  HttpStatus.BAD_REQUEST,
                  "Nieobsługiwany parametr filtra: " + key
          );
      }
    }

    return taskService.getAllTasksWithParams(filter, pageable);
  }


  @GetMapping("/{id}")
  public TaskResponseDTO getTaskById(@PathVariable Long id) {
    var task = taskService.findById(id);
    return task.toTaskResponseDTO();
  }


  @PutMapping("/{id}")
  public TaskResponseDTO updateTask(
          @PathVariable Long id,
          @Valid @RequestBody TaskRequestDTO dto
  ) {
    var updated = taskService.updateTask(id, dto);
    return updated.toTaskResponseDTO();
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTask(@PathVariable Long id) {
    taskService.deleteTask(id);
  }
}
