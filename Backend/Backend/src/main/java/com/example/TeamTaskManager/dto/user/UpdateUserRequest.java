package com.example.TeamTaskManager.dto.user;

import com.example.TeamTaskManager.model.UserRole;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private String profilePicture;
    private UserRole role; // Only ADMIN can change roles
}
