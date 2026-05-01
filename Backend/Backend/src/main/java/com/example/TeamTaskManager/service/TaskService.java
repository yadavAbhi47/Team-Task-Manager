package com.example.TeamTaskManager.service;

import com.example.TeamTaskManager.dto.task.TaskRequest;
import com.example.TeamTaskManager.dto.task.TaskResponse;
import com.example.TeamTaskManager.dto.task.UpdateTaskStatusRequest;
import com.example.TeamTaskManager.exception.ResourceNotFoundException;
import com.example.TeamTaskManager.model.*;
import com.example.TeamTaskManager.repository.ProjectRepository;
import com.example.TeamTaskManager.repository.TaskRepository;
import com.example.TeamTaskManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Transactional
    public TaskResponse createTask(TaskRequest request, User currentUser) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", request.getProjectId()));

        // Only ADMIN, MANAGER (owner/manager of project) can create tasks
        if (currentUser.getRole() == UserRole.CLIENT) {
            throw new AccessDeniedException("Clients cannot create tasks");
        }

        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getAssigneeId()));
        }

        User reviewer = null;
        if (request.getReviewerId() != null) {
            reviewer = userRepository.findById(request.getReviewerId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getReviewerId()));
        }

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO)
                .priority(request.getPriority() != null ? request.getPriority() : TaskPriority.MEDIUM)
                .category(request.getCategory())
                .project(project)
                .assignee(assignee)
                .reviewer(reviewer)
                .deadline(request.getDeadline())
                .fixVersion(request.getFixVersion())
                .build();

        return TaskResponse.from(taskRepository.save(task));
    }

    public List<TaskResponse> getAllTasks(User currentUser) {
        List<Task> tasks;
        if (currentUser.getRole() == UserRole.ADMIN) {
            tasks = taskRepository.findAll();
        } else if (currentUser.getRole() == UserRole.MANAGER) {
            // Managers see tasks in their projects + their own tasks
            tasks = taskRepository.findAll().stream()
                    .filter(t -> t.getProject() != null &&
                            (isProjectMember(t.getProject(), currentUser)
                                    || (t.getAssignee() != null && t.getAssignee().getId().equals(currentUser.getId()))))
                    .collect(Collectors.toList());
        } else {
            tasks = taskRepository.findByAssignee(currentUser);
        }
        return tasks.stream().map(TaskResponse::from).collect(Collectors.toList());
    }

    public TaskResponse getTaskById(Long id, User currentUser) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
        return TaskResponse.from(task);
    }

    public List<TaskResponse> getTasksByProject(Long projectId, User currentUser) {
        projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        return taskRepository.findByProjectIdOrderByCreatedAtDesc(projectId)
                .stream().map(TaskResponse::from).collect(Collectors.toList());
    }

    public List<TaskResponse> getMyTasks(User currentUser) {
        return taskRepository.findByAssignee(currentUser)
                .stream().map(TaskResponse::from).collect(Collectors.toList());
    }

    public List<TaskResponse> getOverdueTasks(User currentUser) {
        List<Task> tasks;
        if (currentUser.getRole() == UserRole.ADMIN || currentUser.getRole() == UserRole.MANAGER) {
            tasks = taskRepository.findOverdueTasks(LocalDateTime.now());
        } else {
            tasks = taskRepository.findOverdueTasksByUser(LocalDateTime.now(), currentUser);
        }
        return tasks.stream().map(TaskResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest request, User currentUser) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        // Only ADMIN, project manager, or assignee can update
        if (!canModifyTask(task, currentUser)) {
            throw new AccessDeniedException("You don't have permission to update this task");
        }

        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getStatus() != null) task.setStatus(request.getStatus());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        if (request.getCategory() != null) task.setCategory(request.getCategory());
        if (request.getDeadline() != null) task.setDeadline(request.getDeadline());
        if (request.getFixVersion() != null) task.setFixVersion(request.getFixVersion());

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getAssigneeId()));
            task.setAssignee(assignee);
        }

        if (request.getReviewerId() != null) {
            User reviewer = userRepository.findById(request.getReviewerId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getReviewerId()));
            task.setReviewer(reviewer);
        }

        return TaskResponse.from(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateTaskStatus(Long id, UpdateTaskStatusRequest request, User currentUser) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
        task.setStatus(request.getStatus());
        return TaskResponse.from(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long id, User currentUser) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        if (currentUser.getRole() != UserRole.ADMIN && !canModifyTask(task, currentUser)) {
            throw new AccessDeniedException("You don't have permission to delete this task");
        }
        taskRepository.deleteById(id);
    }

    private boolean canModifyTask(Task task, User user) {
        if (user.getRole() == UserRole.ADMIN) return true;
        boolean isAssignee = task.getAssignee() != null && task.getAssignee().getId().equals(user.getId());
        boolean isReviewer = task.getReviewer() != null && task.getReviewer().getId().equals(user.getId());
        boolean isProjectMgr = task.getProject() != null && isProjectMember(task.getProject(), user);
        return isAssignee || isReviewer || isProjectMgr;
    }

    private boolean isProjectMember(Project project, User user) {
        boolean isOwner = project.getOwner() != null && project.getOwner().getId().equals(user.getId());
        boolean isManager = project.getManager() != null && project.getManager().getId().equals(user.getId());
        return isOwner || isManager;
    }
}
