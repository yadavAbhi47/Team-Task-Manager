package com.example.TeamTaskManager.controller;

import com.example.TeamTaskManager.dto.dashboard.DashboardResponse;
import com.example.TeamTaskManager.exception.ApiResponse;
import com.example.TeamTaskManager.model.User;
import com.example.TeamTaskManager.service.DashboardService;
import com.example.TeamTaskManager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getDashboard(currentUser)));
    }
}
