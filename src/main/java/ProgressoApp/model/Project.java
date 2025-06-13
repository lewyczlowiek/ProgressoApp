package ProgressoApp.model;


import ProgressoApp.dto.request.ProjectRequestDTO;
import ProgressoApp.dto.response.ProjectResponseDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
@Entity
@Table(name = "projects")
public class Project implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "project_id")
  private Long projectId;

  @NotBlank(message = "Project name cannot be empty!")
  @Size(min = 3, max = 50, message = "Project name must be between {min} and {max} characters!")
  @Column(nullable = false, length = 50)
  private String name;

  @Column(length = 1000)
  private String description;

  @Column(name = "end_date_time")
  private LocalDateTime endDateTime;  // Data i godzina zakończenia projektu.

  @Column(name = "statusProject")
  private String statusProject;  // Zmieniono z boolean na String ("active", "inactive")

  @CreationTimestamp
  @Column(name = "creation_timestamp", nullable = false, updatable = false)
  private LocalDateTime creationTimestamp;

  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnoreProperties({"project"})
  private List<Task> tasks;

  @ManyToMany
  @JoinTable(name = "project_user",
          joinColumns = {@JoinColumn(name = "project_id")},
          inverseJoinColumns = {@JoinColumn(name = "user_id")})
  private Set<User> users;

  public Project() {
  }

  public Project(Project other) {
    this.projectId = other.projectId;
    this.name = other.name;
    this.description = other.description;
    this.creationTimestamp = other.creationTimestamp;
    this.tasks = other.tasks;
    this.users = other.users;
    this.endDateTime = other.endDateTime;
    this.statusProject = other.statusProject;  // Zmieniono z boolean na String
  }

  public Project(ProjectRequestDTO dto) {
    this.name = dto.name();
    this.description = dto.description();
    this.endDateTime = dto.endDateTime();
    this.statusProject = dto.statusProject();  // Zmieniono z boolean na String
  }

  public ProjectResponseDTO toProjectResponseDTO() {
    return new ProjectResponseDTO(
            this.projectId,
            this.name,
            this.description,
            this.creationTimestamp,
            this.endDateTime,
            this.statusProject,  // Zmieniono z boolean na String
            this.tasks != null ? this.tasks.stream().map(Task::toTaskResponseDTO).toList() : List.of(),
            this.users != null ? this.users.stream().map(User::toUserResponseDTO)
                    .collect(java.util.stream.Collectors.toSet()) : Set.of()
    );
  }

  // Gettery i settery
  public Set<User> getUsers() {
    return users;
  }

  public void setUsers(Set<User> users) {
    this.users = users;
  }

  public List<Task> getTasks() {
    return tasks;
  }

  public void setTasks(List<Task> tasks) {
    this.tasks = tasks;
  }

  public Long getProjectId() {
    return projectId;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public LocalDateTime getCreationTimestamp() {
    return creationTimestamp;
  }

  public void setCreationTimestamp(LocalDateTime creationTimestamp) {
    this.creationTimestamp = creationTimestamp;
  }

  public LocalDateTime getEndDateTime() {
    return endDateTime;
  }

  public void setEndDateTime(LocalDateTime endDateTime) {
    this.endDateTime = endDateTime;
  }

  public String getStatusProject() {
    return statusProject;  // Zmieniono z boolean na String
  }

  public void setStatusProject(String statusProject) {
    this.statusProject = statusProject;  // Zmieniono z boolean na String
  }

    public Project orElseThrow(Object projektNieIstnieje) {
    return null; // Placeholder for method implementation
    }
}
