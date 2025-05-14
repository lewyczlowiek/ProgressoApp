package ProgressoApp.service;

import ProgressoApp.model.Project;
import ProgressoApp.repository.ProjectRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

  private final ProjectRepository projectRepository;

  @Autowired
  public ProjectService(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  public List<Project> getAllProjects() {
    return projectRepository.findAll();
  }

  public Optional<Project> getProjectById(Long id) {
    return projectRepository.findById(id);
  }

  public Project createProject(Project project) {
    return projectRepository.save(project);
  }

  public Project updateProject(Long id, Project updatedProject) {
    return projectRepository.findById(id)
        .map(project -> {
          project.setName(updatedProject.getName());
          project.setDescription(updatedProject.getDescription());
          return projectRepository.save(project);
        }).orElseThrow(() -> new RuntimeException("Project not found"));
  }

  public void deleteProject(Long id) {
    Project project = projectRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
    projectRepository.delete(project);
  }
}