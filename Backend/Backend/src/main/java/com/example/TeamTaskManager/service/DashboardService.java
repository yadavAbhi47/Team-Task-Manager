package com.example.TeamTaskManager.service;

import com.example.TeamTaskManager.dto.dashboard.DashboardResponse;
import com.example.TeamTaskManager.dto.task.TaskResponse;
import com.example.TeamTaskManager.model.*;
import com.example.TeamTaskManager.repository.ProjectRepository;
import com.example.TeamTaskManager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public DashboardResponse getDashboard(User currentUser) {
        List<Task> allAccessibleTasks;
        List<Project> allAccessibleProjects;

        if (currentUser.getRole() == UserRole.ADMIN) {
            allAccessibleTasks = taskRepository.findAll();
            allAccessibleProjects = projectRepository.findAll();
        } else {
            allAccessibleTasks = taskRepository.findAll().stream()
                    .filter(t -> isTaskVisible(t, currentUser))
                    .collect(Collectors.toList());
            allAccessibleProjects = projectRepository.findProjectsByUser(currentUser);
        }

        List<Task> myTasks = allAccessibleTasks.stream()
                .filter(t -> t.getAssignee() != null && t.getAssignee().getId().equals(currentUser.getId()))
                .collect(Collectors.toList());

        LocalDateTime now = LocalDateTime.now();
        List<Task> overdueTasks = allAccessibleTasks.stream()
                .filter(t -> t.getDeadline() != null && t.getDeadline().isBefore(now) && t.getStatus() != TaskStatus.DONE)
                .collect(Collectors.toList());

        // Tasks by status
        Map<String, Long> tasksByStatus = allAccessibleTasks.stream()
                .collect(Collectors.groupingBy(t -> t.getStatus().name(), Collectors.counting()));

        // Tasks by priority
        Map<String, Long> tasksByPriority = allAccessibleTasks.stream()
                .collect(Collectors.groupingBy(t -> t.getPriority().name(), Collectors.counting()));

        // Top 5 overdue tasks
        List<TaskResponse> overdueTaskResponses = overdueTasks.stream()
                .sorted((a, b) -> a.getDeadline().compareTo(b.getDeadline()))
                .limit(5)
                .map(TaskResponse::from)
                .collect(Collectors.toList());

        // Top 5 recent my tasks
        List<TaskResponse> recentMyTasks = myTasks.stream()
                .sorted((a, b) -> {
                    if (b.getUpdatedAt() == null) return -1;
                    if (a.getUpdatedAt() == null) return 1;
                    return b.getUpdatedAt().compareTo(a.getUpdatedAt());
                })
                .limit(5)
                .map(TaskResponse::from)
                .collect(Collectors.toList());

        long completedProjects = allAccessibleProjects.stream()
                .filter(p -> p.getStatus() == ProjectStatus.DONE).count();
        long activeProjects = allAccessibleProjects.stream()
                .filter(p -> p.getStatus() == ProjectStatus.IN_PROGRESS).count();

        long completedTasks = allAccessibleTasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.DONE).count();
        long inProgressTasks = allAccessibleTasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();

        return DashboardResponse.builder()
                .totalProjects(allAccessibleProjects.size())
                .activeProjects(activeProjects)
                .completedProjects(completedProjects)
                .totalTasks(allAccessibleTasks.size())
                .myTasks(myTasks.size())
                .overdueTasksCount(overdueTasks.size())
                .completedTasks(completedTasks)
                .inProgressTasks(inProgressTasks)
                .tasksByStatus(tasksByStatus)
                .tasksByPriority(tasksByPriority)
                .overdueTasks(overdueTaskResponses)
                .recentMyTasks(recentMyTasks)
                .build();
    }

    private boolean isTaskVisible(Task task, User user) {
        if (task.getAssignee() != null && task.getAssignee().getId().equals(user.getId())) return true;
        if (task.getReviewer() != null && task.getReviewer().getId().equals(user.getId())) return true;
        if (task.getProject() != null) {
            Project p = task.getProject();
            if (p.getOwner() != null && p.getOwner().getId().equals(user.getId())) return true;
            if (p.getManager() != null && p.getManager().getId().equals(user.getId())) return true;
            if (p.getMembers() != null && p.getMembers().stream().anyMatch(m -> m.getId().equals(user.getId()))) return true;
        }
        return false;
    }
}
