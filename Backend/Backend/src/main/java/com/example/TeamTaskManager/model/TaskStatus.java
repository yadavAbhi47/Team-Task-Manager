package com.example.TeamTaskManager.model;

public enum TaskStatus {
    TODO(1), IN_PROGRESS(2), ON_REVIEW(3), REOPENED(4),
    READY_TO_MERGE(5), MERGED_TO_MASTER(6), DEV_DEPLOYED(7),
    DEV_VERIFIED(8), STAGE_DEVELOPED(9), STAGE_VERIFIED(10),
    PROD_DEPLOYED(11), PROD_VERIFIED(12), DONE(13);
    private final int value;
    TaskStatus(int value) { this.value = value; }
    public int getValue() { return value; }
}
