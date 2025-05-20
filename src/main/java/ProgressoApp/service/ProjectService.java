package ProgressoApp.service;

import ProgressoApp.dto.request.ProjectRequestDTO;
import ProgressoApp.model.Project;
import ProgressoApp.repository.ProjectRepository;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ProjectService {

  private final ProjectRepository projectRepository;

  @Autowired
  public ProjectService(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  public Project createProject(ProjectRequestDTO projectdto) {
    Project project = new Project(projectdto);
    return projectRepository.save(project);
  }

  public List<Project> getAllProjects() {
    return projectRepository.findAll();
  }

}