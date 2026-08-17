package com.analaizer.taskmanager.adapter.in.rest;

import com.analaizer.taskmanager.domain.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeStatusRequest(@NotNull TaskStatus status) {
}
