package com.example.TeamTaskManager.repository;

import com.example.TeamTaskManager.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProject(Project project);

    List<Task> findByProjectId(Long projectId);

    List<Task> findByAssignee(User assignee);

    List<Task> findByAssigneeId(Long assigneeId);

    List<Task> findByReviewer(User reviewer);

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByPriority(TaskPriority priority);

    // Overdue tasks: deadline passed, not completed
    @Query("SELECT t FROM Task t WHERE t.deadline < :now AND t.status != 'DONE'")
    List<Task> findOverdueTasks(@Param("now") LocalDateTime now);

    // Overdue tasks for a specific user
    @Query("SELECT t FROM Task t WHERE t.deadline < :now AND t.status != 'DONE' AND t.assignee = :user")
    List<Task> findOverdueTasksByUser(@Param("now") LocalDateTime now, @Param("user") User user);

    List<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status);

    List<Task> findByProjectIdAndAssigneeId(Long projectId, Long assigneeId);

    // Count tasks by status for dashboard
    long countByStatus(TaskStatus status);

    long countByAssignee(User assignee);

    long countByAssigneeAndStatus(User assignee, TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.assignee = :user ORDER BY t.updatedAt DESC")
    List<Task> findRecentTasksByUser(@Param("user") User user);

    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId ORDER BY t.createdAt DESC")
    List<Task> findByProjectIdOrderByCreatedAtDesc(@Param("projectId") Long projectId);
}
