package ProgressoApp.service;

import ProgressoApp.model.Task;

public interface TaskService {

  void createTask(Task task);

  void updateTask(Task task);

  void deleteTask(Long taskId);

  Task getTaskById(Long taskId);
}
