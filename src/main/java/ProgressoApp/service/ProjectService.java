package ProgressoApp.service;

import ProgressoApp.model.Project;

public interface ProjectService {

  void createProject(Project project);

  void updateProject(Project project);

  void deleteProject(Long projectId);

  Project getProjectById(Long projectId);
}