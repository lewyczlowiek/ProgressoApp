package ProgressoApp.service.impl;

import ProgressoApp.model.Project;
import ProgressoApp.repository.ProjectRepository;
import ProgressoApp.service.ProjectService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl implements ProjectService {

  private final ProjectRepository projectRepository;

  @Autowired
  public ProjectServiceImpl(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  @Override
  public void createProject(Project project) {
    projectRepository.save(project);
  }

  @Override
  public void updateProject(Project project) {
    projectRepository.save(project);
  }

  @Override
  public void deleteProject(Long projectId) {
    projectRepository.deleteById(projectId);
  }

  public Project getProjectById(Long projectId) {
    Optional<Project> project = projectRepository.findById(projectId);
    if (project.isPresent()) {
      return project.get(); // Zwraca projekt, jeżeli istnieje
    } else {
      throw new RuntimeException(
          "Project not found with id " + projectId); // Jeśli projekt nie istnieje, rzucamy wyjątek
    }
  }
}