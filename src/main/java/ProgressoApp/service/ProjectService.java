package ProgressoApp.service;

import ProgressoApp.dto.request.ProjectRequestDTO;
import ProgressoApp.dto.response.ProjectResponseDTO;
import ProgressoApp.model.Project;
import ProgressoApp.model.Task;
import ProgressoApp.model.User;
import ProgressoApp.repository.ProjectRepository;
import ProgressoApp.repository.TaskRepository;
import ProgressoApp.utils.ProjectSpecification;
import ProgressoApp.utils.SearchCriteria;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.transaction.Transactional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class ProjectService {

  private final ProjectRepository projectRepository;
  private final UserService userService;
  private final TaskRepository taskRepository;

  @Autowired
  public ProjectService(ProjectRepository projectRepository, TaskRepository taskRepository,
      UserService userService) {
    this.projectRepository = projectRepository;
    this.userService = userService;
    this.taskRepository = taskRepository;
  }

  public Project createProject(ProjectRequestDTO projectdto) {
    Project project = new Project(projectdto);
    return projectRepository.save(project);
  }


  public Page<ProjectResponseDTO> getAllProjects(Pageable pageable) {
    Page<Project> page = projectRepository.findAll(pageable);
    return page.map(Project::toProjectResponseDTO);
  }

  public Page<ProjectResponseDTO> getAllProjectsWithParams(Map<String, Object> filter,
      Pageable pageable) {
    List<Specification<Project>> specs = filter.entrySet().stream()
        .map(entry -> (Specification<Project>) new ProjectSpecification(
            new SearchCriteria(entry.getKey(), entry.getValue())))
        .toList();

    Specification<Project> finalSpec = specs.stream()
        .reduce(Specification.where(null), Specification::and);

    Page<Project> page = projectRepository.findAll(finalSpec, pageable);
    return page.map(Project::toProjectResponseDTO);
  }

  public Project findById(long id) {
    return projectRepository.findByProjectId(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
  }

  public Project updateProject(long id, ProjectRequestDTO projectDTO) {
    Project project = projectRepository.findByProjectId(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

    project.setName(projectDTO.name());
    project.setDescription(projectDTO.description());
    
    if (projectDTO.users() != null) {
      Set<User> existingUsers = project.getUsers();
      if (existingUsers == null) {
        existingUsers = new HashSet<>();
        project.setUsers(existingUsers);
      } else {
        existingUsers.clear();
      }
      Set<User> updatedUsers = projectDTO.users().stream()
          .map(userDto -> userService.findById(userDto.userId()))
          .collect(Collectors.toSet());
      existingUsers.addAll(updatedUsers);
    } else {
      project.getUsers().clear();
    }

    // Update tasks (modyfikujemy istniejącą listę)
    if (projectDTO.tasks() != null) {
      List<Task> existingTasks = project.getTasks();
      if (existingTasks == null) {
        existingTasks = new ArrayList<>();
        project.setTasks(existingTasks);
      } else {
        existingTasks.clear();
      }
      List<Task> updatedTasks = projectDTO.tasks().stream()
          .map(taskDto -> {
            Task task = taskRepository.findById(taskDto.id())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
            task.setProject(project);
            return task;
          })
          .collect(Collectors.toList());
      existingTasks.addAll(updatedTasks);
    } else {
      project.getTasks().clear();
    }

    return projectRepository.save(project);
  }


  public Project deleteProject(long id) {
    Project project = projectRepository.findByProjectId(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

    // Usuń powiązania z użytkownikami przed usunięciem projektu
    project.getUsers().clear();

    projectRepository.delete(project);
    return project;
  }

}