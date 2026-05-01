package com.example.TeamTaskManager.model;

public enum UserRole {
    ADMIN(1), MANAGER(2), EMPLOYEE(3), CLIENT(4);
    private final int value;
    UserRole(int value) { this.value = value; }
    public int getValue() { return value; }
}
