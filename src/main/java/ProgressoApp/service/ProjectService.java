package ProgressoApp.service;

import ProgressoApp.model.Project;
import java.util.List;
import java.util.Optional;

public interface ProjectService {

  List<Project> getAllProjects();

  Optional<Project> getProjectById(Long id);

  Project createProject(Project project);

  Project updateProject(Long id, Project updatedProject);

  void deleteProject(Long id);
}