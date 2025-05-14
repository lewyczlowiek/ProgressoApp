package ProgressoApp.service;

import ProgressoApp.dto.request.TaskRequestDTO;
import ProgressoApp.model.Task;
import java.util.Optional;


public interface TaskService {

  Optional<Task> getTaskById(Long id);

  Task createTask(TaskRequestDTO dto);

  Task updateTask(Long id, Task updatedTask);

  void deleteTask(Long id);
}
