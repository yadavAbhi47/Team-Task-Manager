package com.example.TeamTaskManager.model;

public enum ProjectStatus {
    TODO(1), IN_PROGRESS(2), DONE(3), CANCELED(4);
    private final int value;
    ProjectStatus(int value) { this.value = value; }
    public int getValue() { return value; }
}
