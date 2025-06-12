package ProgressoApp.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record TaskSubmissionResponseDTO(Long id,
                                        Long taskId,
                                        Long userId,
                                        String filePath,
                                        Double grade,
                                        String feedback,
                                        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
                                        LocalDateTime submittedAt) {

}
