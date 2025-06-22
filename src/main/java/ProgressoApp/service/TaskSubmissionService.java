package ProgressoApp.service;

import ProgressoApp.dto.request.TaskRequestDTO;
import ProgressoApp.dto.request.TaskSubmissionRequestDTO;
import ProgressoApp.model.Project;
import ProgressoApp.model.Task;
import ProgressoApp.model.TaskSubmission;
import ProgressoApp.model.User;
import ProgressoApp.repository.TaskSubmissionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class TaskSubmissionService {

  private final TaskSubmissionRepository taskSubmissionRepository;
  private final UserService userService;
  private final TaskService taskService;

  public TaskSubmissionService(TaskSubmissionRepository taskSubmissionRepository,
      UserService userService, TaskService taskService) {
    this.taskSubmissionRepository = taskSubmissionRepository;
    this.userService = userService;
    this.taskService = taskService;
  }

  public TaskSubmission createTaskSubmission(TaskSubmissionRequestDTO dto) {

    User user = new User(userService.findById(dto.userId()));
    Task task = new Task(taskService.findById(dto.taskId()));

    TaskSubmission taskSubmission = new TaskSubmission(dto, user, task);

    return taskSubmissionRepository.save(taskSubmission);
  }
}
