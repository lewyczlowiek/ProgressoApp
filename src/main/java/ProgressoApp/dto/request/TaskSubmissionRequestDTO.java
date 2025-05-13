package ProgressoApp.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record TaskSubmissionRequestDTO(
    Long taskId,
    Long userId,
    String filePath,
    Double grade,
    String feedback,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    LocalDateTime submittedAt
) {

}