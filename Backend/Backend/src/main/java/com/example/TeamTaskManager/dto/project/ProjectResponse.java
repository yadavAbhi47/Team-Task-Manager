package com.example.TeamTaskManager.dto.project;

import com.example.TeamTaskManager.dto.user.UserResponse;
import com.example.TeamTaskManager.model.Project;
import com.example.TeamTaskManager.model.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {
    private Long id;
    private String title;
    private String description;
    private ProjectStatus status;
    private UserResponse owner;
    private UserResponse manager;
    private List<UserResponse> members;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime expectedEndTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProjectResponse from(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .status(project.getStatus())
                .owner(UserResponse.from(project.getOwner()))
                .manager(UserResponse.from(project.getManager()))
                .members(project.getMembers() != null
                        ? project.getMembers().stream().map(UserResponse::from).collect(Collectors.toList())
                        : List.of())
                .startTime(project.getStartTime())
                .endTime(project.getEndTime())
                .expectedEndTime(project.getExpectedEndTime())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
}
