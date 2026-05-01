package com.example.TeamTaskManager.dto.task;

import com.example.TeamTaskManager.model.TaskCategory;
import com.example.TeamTaskManager.model.TaskPriority;
import com.example.TeamTaskManager.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Status is required")
    private TaskStatus status;

    @NotNull(message = "Priority is required")
    private TaskPriority priority;

    private TaskCategory category;

    @NotNull(message = "Project ID is required")
    private Long projectId;

    private Long assigneeId;
    private Long reviewerId;

    private LocalDateTime deadline;
    private String fixVersion;
}
