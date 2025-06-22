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
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

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

  public List<Project> findAll() {
    return projectRepository.findAll();
  }

  public Project updateProject(long id, ProjectRequestDTO projectDTO) {
    Project project = projectRepository.findByProjectId(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

    // Aktualizacja danych projektu
    project.setName(projectDTO.name());
    project.setDescription(projectDTO.description());
    project.setEndDateTime(projectDTO.endDateTime());

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

    // Aktualizacja zadań
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
                .orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
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

  public void deleteProject(long id) {
    Project project = projectRepository.findByProjectId(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

    projectRepository.delete(project);  // Usuwanie projektu z repozytorium
  }


  public Project findProjectById(long id) {
    return projectRepository.findByProjectId(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
  }


  // Sortowanie i filtrowanie po nazwie (dla widoku i controller'a)
  public Page<ProjectResponseDTO> getAllProjects(Pageable pageable, String sort, String dir) {
    Sort.Direction direction =
        "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
    Pageable sortedPageable = PageRequest.of(
        pageable.getPageNumber(),
        pageable.getPageSize(),
        Sort.by(direction, sort)
    );
    Page<Project> page = projectRepository.findAll(sortedPageable);
    return page.map(Project::toProjectResponseDTO);
  }

  public Page<ProjectResponseDTO> getProjectsByNameContaining(String name, Pageable pageable,
      String sort, String dir) {
    Sort.Direction direction =
        "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
    Pageable sortedPageable = PageRequest.of(
        pageable.getPageNumber(),
        pageable.getPageSize(),
        Sort.by(direction, sort)
    );
    Page<Project> page = projectRepository.findByNameContainingIgnoreCase(name, sortedPageable);
    return page.map(Project::toProjectResponseDTO);
  }

  public Page<ProjectResponseDTO> getProjectsForUser(String username, Pageable pageable) {
    Page<Project> projectsPage = projectRepository.findByUserEmail(username, pageable);
    return projectsPage.map(Project::toProjectResponseDTO);
  }

  public Page<ProjectResponseDTO> getProjectsByNameContainingForUser(String username, String search,
      Pageable pageable) {
    Page<Project> projectsPage = projectRepository.findByUserEmailAndNameContaining(
        username,
        search, pageable);
    return projectsPage.map(Project::toProjectResponseDTO);
  }
}