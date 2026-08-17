package com.analaizer.taskmanager.adapter.in.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTaskRequest(
        @NotBlank String title,
        @NotNull String description,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        BigDecimal budget
) {
}
