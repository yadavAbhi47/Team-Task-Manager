package com.example.TeamTaskManager.repository;

import com.example.TeamTaskManager.model.Project;
import com.example.TeamTaskManager.model.ProjectStatus;
import com.example.TeamTaskManager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByOwner(User owner);

    List<Project> findByManager(User manager);

    List<Project> findByStatus(ProjectStatus status);

    @Query("SELECT p FROM Project p WHERE p.owner = :user OR p.manager = :user OR :user MEMBER OF p.members")
    List<Project> findProjectsByUser(@Param("user") User user);

    @Query("SELECT p FROM Project p WHERE :user MEMBER OF p.members")
    List<Project> findByMembersContaining(@Param("user") User user);

    long countByStatus(ProjectStatus status);
}
