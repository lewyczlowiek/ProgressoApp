package ProgressoApp.dto.request;

import ProgressoApp.dto.response.TaskSubmissionResponseDTO;
import ProgressoApp.model.TaskStatus;
import java.time.LocalDate;

public record TaskRequestDTO(String name,
                             String description,
                             Integer taskOrder,
                             TaskStatus taskStatus,
                             Long projectId,
                             LocalDate dueDate
) {


}
