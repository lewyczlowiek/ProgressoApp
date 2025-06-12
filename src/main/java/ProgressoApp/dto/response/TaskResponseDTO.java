package ProgressoApp.dto.response;

import ProgressoApp.model.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record TaskResponseDTO(long id, String name, String description, String taskOrder,
                              TaskStatus taskStatus, long projectId,
                              @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
                              LocalDateTime creationTimestamp,
                              @JsonFormat(pattern = "yyyy-MM-dd")
                              LocalDate dueDate,
                              List<TaskSubmissionResponseDTO> taskSubmission) {

}
