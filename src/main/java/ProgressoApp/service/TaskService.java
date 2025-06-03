package ProgressoApp.service;

import ProgressoApp.dto.request.TaskRequestDTO;
import ProgressoApp.model.Project;
import ProgressoApp.model.Task;
import ProgressoApp.repository.ProjectRepository;
import ProgressoApp.repository.TaskRepository;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TaskService {

  private final TaskRepository taskRepository;
  private final ProjectRepository projectRepository;

  @Autowired
  public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository) {
    this.taskRepository = taskRepository;
    this.projectRepository = projectRepository;
  }


  public Task findById(long id) {
    return taskRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
  }


  public Task createTask(TaskRequestDTO dto) {
    Task task = new Task();

    return taskRepository.save(task);
  }

  public Task updateTask(Long id, Task updatedTask) {
    return taskRepository.findById(id)
        .map(task -> {
          task.setName(updatedTask.getName());
          task.setDescription(updatedTask.getDescription());
          task.setTaskOrder(updatedTask.getTaskOrder());
          task.setTaskStatus(updatedTask.getTaskStatus());
          return taskRepository.save(task);
        }).orElseThrow(() -> new RuntimeException("Task not found"));
  }

  public void deleteTask(Long id) {
    taskRepository.deleteById(id);
  }

}
