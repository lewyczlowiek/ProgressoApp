package ProgressoApp.service.impl;

import ProgressoApp.dto.TaskDTO;
import ProgressoApp.model.Project;
import ProgressoApp.model.Task;
import ProgressoApp.model.TaskStatus;
import ProgressoApp.repository.ProjectRepository;
import ProgressoApp.repository.TaskRepository;
import ProgressoApp.service.TaskService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService {

  private final TaskRepository taskRepository;
  private final ProjectRepository projectRepository;

  @Autowired
  public TaskServiceImpl(TaskRepository taskRepository, ProjectRepository projectRepository) {
    this.taskRepository = taskRepository;
    this.projectRepository = projectRepository;
  }

  public List<Task> getAllTasks() {
    return taskRepository.findAll();
  }

  public List<Task> getAllTasksForProject(Long projectId) {
    return taskRepository.findByProject_ProjectId(projectId);
  }

  public Optional<Task> getTaskById(Long id) {
    return taskRepository.findById(id);
  }


  public Task createTask(TaskDTO dto) {
    Task task = new Task();
    task.setName(dto.getName());
    task.setDescription(dto.getDescription());
    task.setTaskOrder(dto.getTaskOrder());
    task.setTaskStatus(TaskStatus.TO_DO);

    if (dto.getProjectId() != null) {
      Project project = projectRepository.findById(dto.getProjectId())
          .orElseThrow(
              () -> new RuntimeException("Project not found with id: " + dto.getProjectId()));
      task.setProject(project);
    }

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
