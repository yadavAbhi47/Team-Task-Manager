package com.example.TeamTaskManager.service;

import com.example.TeamTaskManager.dto.project.ProjectRequest;
import com.example.TeamTaskManager.dto.project.ProjectResponse;
import com.example.TeamTaskManager.exception.ResourceNotFoundException;
import com.example.TeamTaskManager.model.*;
import com.example.TeamTaskManager.repository.ProjectRepository;
import com.example.TeamTaskManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProjectResponse createProject(ProjectRequest request, User currentUser) {
        // Only ADMIN or MANAGER can create projects
        if (currentUser.getRole() == UserRole.EMPLOYEE || currentUser.getRole() == UserRole.CLIENT) {
            throw new AccessDeniedException("Only ADMIN or MANAGER can create projects");
        }

        User manager = null;
        if (request.getManagerId() != null) {
            manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getManagerId()));
        }

        List<User> members = new ArrayList<>();
        if (request.getMemberIds() != null && !request.getMemberIds().isEmpty()) {
            members = userRepository.findAllById(request.getMemberIds());
        }

        Project project = Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : ProjectStatus.TODO)
                .owner(currentUser)
                .manager(manager)
                .members(members)
                .startTime(request.getStartTime())
                .expectedEndTime(request.getExpectedEndTime())
                .build();

        return ProjectResponse.from(projectRepository.save(project));
    }

    public List<ProjectResponse> getAllProjects(User currentUser) {
        List<Project> projects;
        if (currentUser.getRole() == UserRole.ADMIN) {
            projects = projectRepository.findAll();
        } else {
            projects = projectRepository.findProjectsByUser(currentUser);
        }
        return projects.stream().map(ProjectResponse::from).collect(Collectors.toList());
    }

    public ProjectResponse getProjectById(Long id, User currentUser) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        checkProjectAccess(project, currentUser);
        return ProjectResponse.from(project);
    }

    @Transactional
    public ProjectResponse updateProject(Long id, ProjectRequest request, User currentUser) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        // Only owner, manager, or ADMIN can update
        if (!isProjectManager(project, currentUser) && currentUser.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("You don't have permission to update this project");
        }

        if (request.getTitle() != null) project.setTitle(request.getTitle());
        if (request.getDescription() != null) project.setDescription(request.getDescription());
        if (request.getStatus() != null) project.setStatus(request.getStatus());
        if (request.getStartTime() != null) project.setStartTime(request.getStartTime());
        if (request.getExpectedEndTime() != null) project.setExpectedEndTime(request.getExpectedEndTime());

        if (request.getManagerId() != null) {
            User manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getManagerId()));
            project.setManager(manager);
        }

        if (request.getMemberIds() != null) {
            List<User> members = userRepository.findAllById(request.getMemberIds());
            project.setMembers(members);
        }

        // Mark end time if done
        if (request.getStatus() == ProjectStatus.DONE && project.getEndTime() == null) {
            project.setEndTime(java.time.LocalDateTime.now());
        }

        return ProjectResponse.from(projectRepository.save(project));
    }

    @Transactional
    public void deleteProject(Long id, User currentUser) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        if (!project.getOwner().getId().equals(currentUser.getId())
                && currentUser.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Only the project owner or ADMIN can delete a project");
        }
        projectRepository.deleteById(id);
    }

    @Transactional
    public ProjectResponse addMember(Long projectId, Long userId, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        if (!isProjectManager(project, currentUser) && currentUser.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Only project manager or ADMIN can add members");
        }

        User newMember = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!project.getMembers().contains(newMember)) {
            project.getMembers().add(newMember);
        }
        return ProjectResponse.from(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse removeMember(Long projectId, Long userId, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        if (!isProjectManager(project, currentUser) && currentUser.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Only project manager or ADMIN can remove members");
        }

        project.getMembers().removeIf(m -> m.getId().equals(userId));
        return ProjectResponse.from(projectRepository.save(project));
    }

    private void checkProjectAccess(Project project, User user) {
        if (user.getRole() == UserRole.ADMIN) return;
        boolean isOwner = project.getOwner() != null && project.getOwner().getId().equals(user.getId());
        boolean isManager = project.getManager() != null && project.getManager().getId().equals(user.getId());
        boolean isMember = project.getMembers().stream().anyMatch(m -> m.getId().equals(user.getId()));
        if (!isOwner && !isManager && !isMember) {
            throw new AccessDeniedException("You don't have access to this project");
        }
    }

    private boolean isProjectManager(Project project, User user) {
        boolean isOwner = project.getOwner() != null && project.getOwner().getId().equals(user.getId());
        boolean isManager = project.getManager() != null && project.getManager().getId().equals(user.getId());
        return isOwner || isManager;
    }
}
