package com.analaizer.taskmanager.adapter.in.rest;

import jakarta.validation.constraints.NotBlank;

public record LabelRequest(@NotBlank String label) {
}
