package com.example.TeamTaskManager.dto.task;

import com.example.TeamTaskManager.dto.user.UserResponse;
import com.example.TeamTaskManager.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private TaskCategory category;
    private Long projectId;
    private String projectTitle;
    private UserResponse assignee;
    private UserResponse reviewer;
    private LocalDateTime deadline;
    private String fixVersion;
    private boolean overdue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TaskResponse from(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .category(task.getCategory())
                .projectId(task.getProject() != null ? task.getProject().getId() : null)
                .projectTitle(task.getProject() != null ? task.getProject().getTitle() : null)
                .assignee(UserResponse.from(task.getAssignee()))
                .reviewer(UserResponse.from(task.getReviewer()))
                .deadline(task.getDeadline())
                .fixVersion(task.getFixVersion())
                .overdue(task.getDeadline() != null
                        && task.getDeadline().isBefore(LocalDateTime.now())
                        && task.getStatus() != TaskStatus.DONE)
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
