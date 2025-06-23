package ProgressoApp.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record TaskSubmissionResponseDTO(Long id,
                                        Long taskId,
                                        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
                                        LocalDateTime submittedAt) {

}
