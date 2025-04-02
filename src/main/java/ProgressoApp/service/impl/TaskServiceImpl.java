package ProgressoApp.service.impl;

import ProgressoApp.model.Task;
import ProgressoApp.repository.TaskRepository;
import ProgressoApp.service.TaskService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService {

  private final TaskRepository taskRepository;

  @Autowired
  public TaskServiceImpl(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  @Override
  public void createTask(Task task) {

    Task newTask = new Task(task.getName(), task.getDescription(), task.getTaskOrder(),
        task.getCreationTimestamp());
    taskRepository.save(newTask);
  }

  @Override
  public void updateTask(Task task) {
    taskRepository.save(task);
  }

  @Override
  public void deleteTask(Long taskId) {
    taskRepository.deleteById(taskId);
  }

  @Override
  public Task getTaskById(Long taskId) {
    Optional<Task> task = taskRepository.findById(taskId);
    if (task.isPresent()) {
      return task.get(); // Zwraca zadanie, jeżeli istnieje
    } else {
      throw new RuntimeException(
          "Task not found with id " + taskId); // Jeśli zadanie nie istnieje, rzucamy wyjątek
    }
  }

}
