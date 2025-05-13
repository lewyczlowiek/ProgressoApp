package ProgressoApp.service;

import ProgressoApp.dto.request.TaskRequestDTO;
import ProgressoApp.model.Task;
import java.util.Optional;
import org.h2.mvstore.Page;

public interface TaskService {

  Page<Task> getAllTasks();

  Page<Task> getAllTasksForProject(Long id);

  Optional<Task> getTaskById(Long id);

  Task createTask(TaskRequestDTO dto);

  Task updateTask(Long id, Task updatedTask);

  void deleteTask(Long id);
}
