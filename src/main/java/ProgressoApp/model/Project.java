package ProgressoApp.model;


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

  @CreationTimestamp
  @Column(name = "creation_timestamp", nullable = false, updatable = false)
  private LocalDateTime creationTimestamp;

  @Column(name = "due_date")
  private LocalDate dueDate;

  @OneToMany(mappedBy = "project")
  @JsonIgnoreProperties({"project"})
  private List<Task> tasks;

  @ManyToMany
  @JoinTable(name = "project_user",
          joinColumns = {@JoinColumn(name = "project_id")},
          inverseJoinColumns = {@JoinColumn(name = "user_id")})
  private Set<User> users;


  public Project(String description, String name, LocalDateTime creationTimestamp,
      LocalDate dueDate) {
    this.description = description;
    this.name = name;
    this.creationTimestamp = creationTimestamp;
    this.dueDate = dueDate;
  }

  public Project() {

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

  public LocalDate getDueDate() {
    return dueDate;
  }

  public void setDueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
  }
}
