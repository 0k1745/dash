package com.analaizer.taskmanager.adapter.out.github;

import com.analaizer.taskmanager.domain.TaskStatus;
import java.util.Map;

// Status is represented as a mutually-exclusive "status:<value>" label on the
// GitHub issue, per docs/task-manager-github-adapter.md.
public final class GitHubStatusLabels {

    private static final Map<TaskStatus, String> LABEL_BY_STATUS = Map.of(
            TaskStatus.TODO, "status:todo",
            TaskStatus.ANALYSIS, "status:analysis",
            TaskStatus.IN_PROGRESS, "status:in-progress",
            TaskStatus.DONE, "status:done"
    );

    private GitHubStatusLabels() {
    }

    public static String labelFor(TaskStatus status) {
        return LABEL_BY_STATUS.get(status);
    }

    public static boolean isStatusLabel(String label) {
        return LABEL_BY_STATUS.containsValue(label);
    }

    public static TaskStatus statusFor(String label) {
        return LABEL_BY_STATUS.entrySet().stream()
                .filter(entry -> entry.getValue().equals(label))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
}
