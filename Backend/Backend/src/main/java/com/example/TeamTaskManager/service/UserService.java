package com.example.TeamTaskManager.service;

import com.example.TeamTaskManager.dto.user.UpdateUserRequest;
import com.example.TeamTaskManager.dto.user.UserResponse;
import com.example.TeamTaskManager.exception.ResourceNotFoundException;
import com.example.TeamTaskManager.model.User;
import com.example.TeamTaskManager.model.UserRole;
import com.example.TeamTaskManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role).stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request, User currentUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // Only admin or the user themselves can update
        if (!currentUser.getRole().equals(UserRole.ADMIN) && !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("You can only update your own profile");
        }

        if (request.getName() != null) user.setName(request.getName());
        if (request.getProfilePicture() != null) user.setProfilePicture(request.getProfilePicture());

        // Only ADMIN can change roles
        if (request.getRole() != null) {
            if (!currentUser.getRole().equals(UserRole.ADMIN)) {
                throw new AccessDeniedException("Only admins can change user roles");
            }
            user.setRole(request.getRole());
        }

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id, User currentUser) {
        if (!currentUser.getRole().equals(UserRole.ADMIN)) {
            throw new AccessDeniedException("Only admins can delete users");
        }
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }
}
