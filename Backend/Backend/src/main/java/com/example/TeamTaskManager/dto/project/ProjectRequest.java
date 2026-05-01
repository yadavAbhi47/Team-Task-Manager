package com.example.TeamTaskManager.dto.project;

import com.example.TeamTaskManager.model.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProjectRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Status is required")
    private ProjectStatus status;

    private Long managerId;
    private LocalDateTime startTime;
    private LocalDateTime expectedEndTime;
    private List<Long> memberIds;
}
