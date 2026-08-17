import { useCallback, useEffect, useMemo, useState } from "react";
import { addLabel } from "../application/add-label";
import { changeTaskStatus } from "../application/change-task-status";
import { createTask } from "../application/create-task";
import { listTasks } from "../application/list-tasks";
import { removeLabel } from "../application/remove-label";
import { searchTasksByLabels } from "../application/search-tasks-by-labels";
import type { Task, TaskStatus } from "../domain/task";
import type { NewTask, TaskRepository } from "../domain/task-repository";
import { TaskForm } from "./TaskForm";
import { TaskItem } from "./TaskItem";

interface TaskListPageProps {
  // Injected by the shell (composition root), which knows how to configure
  // the concrete adapter (e.g. the backend base URL).
  repository: TaskRepository;
}

export function TaskListPage({ repository }: TaskListPageProps) {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [labelFilter, setLabelFilter] = useState("");

  const refresh = useCallback(() => {
    const labels = labelFilter
      .split(",")
      .map((label) => label.trim())
      .filter((label) => label.length > 0);
    const result = labels.length > 0 ? searchTasksByLabels(repository, labels) : listTasks(repository);
    result.then(setTasks).catch((err: Error) => setError(err.message));
  }, [repository, labelFilter]);

  useEffect(() => {
    refresh();
  }, [refresh]);

  const handleCreate = useMemo(
    () => (task: NewTask) => {
      createTask(repository, task)
        .then(() => refresh())
        .catch((err: Error) => setError(err.message));
    },
    [repository, refresh],
  );

  const handleChangeStatus = useMemo(
    () => (id: string, status: TaskStatus) => {
      changeTaskStatus(repository, id, status)
        .then(() => refresh())
        .catch((err: Error) => setError(err.message));
    },
    [repository, refresh],
  );

  const handleAddLabel = useMemo(
    () => (id: string, label: string) => {
      addLabel(repository, id, label)
        .then(() => refresh())
        .catch((err: Error) => setError(err.message));
    },
    [repository, refresh],
  );

  const handleRemoveLabel = useMemo(
    () => (id: string, label: string) => {
      removeLabel(repository, id, label)
        .then(() => refresh())
        .catch((err: Error) => setError(err.message));
    },
    [repository, refresh],
  );

  return (
    <section>
      <h1>Task Manager</h1>
      <TaskForm onSubmit={handleCreate} />
      <label>
        Filter by labels (comma-separated)
        <input
          type="text"
          value={labelFilter}
          onChange={(event) => setLabelFilter(event.target.value)}
          aria-label="Filter by labels"
        />
      </label>
      {error && <p role="alert">{error}</p>}
      <ul>
        {tasks.map((task) => (
          <TaskItem
            key={task.id}
            task={task}
            onChangeStatus={handleChangeStatus}
            onAddLabel={handleAddLabel}
            onRemoveLabel={handleRemoveLabel}
          />
        ))}
      </ul>
    </section>
  );
}
