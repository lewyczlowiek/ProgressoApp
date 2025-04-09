package ProgressoApp.service;

import ProgressoApp.dto.TaskDTO;
import ProgressoApp.model.Task;
import java.util.List;
import java.util.Optional;

public interface TaskService {

  List<Task> getAllTasks();

  List<Task> getAllTasksForProject(Long id);
  Optional<Task> getTaskById(Long id);

  Task createTask(TaskDTO dto);

  Task updateTask(Long id, Task updatedTask);

  void deleteTask(Long id);
}
