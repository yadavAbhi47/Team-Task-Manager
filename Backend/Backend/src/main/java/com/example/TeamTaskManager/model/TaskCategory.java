package com.example.TeamTaskManager.model;

public enum TaskCategory {
    BUG(1), FEATURE(2);
    private final int value;
    TaskCategory(int value) { this.value = value; }
    public int getValue() { return value; }
}
