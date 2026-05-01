package com.example.TeamTaskManager.model;

public enum TaskPriority {
    LOW(1), MEDIUM(2), HIGH(3);
    private final int value;
    TaskPriority(int value) { this.value = value; }
    public int getValue() { return value; }
}
