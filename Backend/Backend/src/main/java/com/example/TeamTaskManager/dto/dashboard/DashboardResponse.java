package com.example.TeamTaskManager.dto.dashboard;

import com.example.TeamTaskManager.dto.task.TaskResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    // Project stats
    private long totalProjects;
    private long activeProjects;
    private long completedProjects;

    // Task stats
    private long totalTasks;
    private long myTasks;
    private long overdueTasksCount;
    private long completedTasks;
    private long inProgressTasks;

    // Task breakdown by status
    private Map<String, Long> tasksByStatus;

    // Task breakdown by priority
    private Map<String, Long> tasksByPriority;

    // Recent overdue tasks (top 5)
    private List<TaskResponse> overdueTasks;

    // Recent tasks assigned to me (top 5)
    private List<TaskResponse> recentMyTasks;
}
