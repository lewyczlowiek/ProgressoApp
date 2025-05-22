package ProgressoApp.service;

import ProgressoApp.dto.request.ProjectRequestDTO;
import ProgressoApp.model.Project;
import ProgressoApp.repository.ProjectRepository;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

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

  public Project getProjectById(long id) {
    return projectRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
  }


}