package ProgressoApp.model;

import ProgressoApp.dto.request.TaskSubmissionRequestDTO;
import ProgressoApp.dto.response.TaskSubmissionResponseDTO;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_submissions")
public class TaskSubmission {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String filePath;

  private LocalDateTime submittedAt;

  private Double grade;

  private String feedback;

  @ManyToOne
  @JoinColumn(name = "task_id", nullable = false)
  private Task task;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  public TaskSubmission() {

  }

  public TaskSubmission(TaskSubmission submission) {
    this.filePath = submission.filePath;
    this.submittedAt = submission.submittedAt;
    this.grade = submission.grade;
    this.feedback = submission.feedback;
    this.task = submission.task;
    this.user = submission.user;
  }

  public TaskSubmission(TaskSubmissionRequestDTO dto, User user, Task task) {
    this.filePath = dto.filePath();
    this.submittedAt = LocalDateTime.now();
    this.user = user;
    this.task = task;
  }

  public TaskSubmissionResponseDTO toTaskSubmissionResponseDTO() {
    return new TaskSubmissionResponseDTO(
        this.id,
        this.task != null ? this.task.getTaskId() : null,
        this.user != null ? this.user.getUserId() : null,
        this.filePath,
        this.grade,
        this.feedback,
        this.submittedAt
    );
  }


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public LocalDateTime getSubmittedAt() {
    return submittedAt;
  }

  public void setSubmittedAt(LocalDateTime submittedAt) {
    this.submittedAt = submittedAt;
  }

  public Double getGrade() {
    return grade;
  }

  public void setGrade(Double grade) {
    this.grade = grade;
  }

  public String getFeedback() {
    return feedback;
  }

  public void setFeedback(String feedback) {
    this.feedback = feedback;
  }

  public Task getTask() {
    return task;
  }

  public void setTask(Task task) {
    this.task = task;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}