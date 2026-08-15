package com.analaizer.taskmanager.domain;

// Kept storage-agnostic on purpose: adapters decide how a status is represented
// (e.g. the GitHub adapter maps each value to a mutually-exclusive "status:*" label).
public enum TaskStatus {
    TODO,
    ANALYSIS,
    IN_PROGRESS,
    DONE
}
