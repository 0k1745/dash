import { useCallback, useEffect, useMemo, useState } from "react";
import { completeTask } from "../application/complete-task";
import { createTask } from "../application/create-task";
import { listTasks } from "../application/list-tasks";
import type { Task } from "../domain/task";
import type { TaskRepository } from "../domain/task-repository";
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

  const refresh = useCallback(() => {
    listTasks(repository)
      .then(setTasks)
      .catch((err: Error) => setError(err.message));
  }, [repository]);

  useEffect(() => {
    refresh();
  }, [refresh]);

  const handleCreate = useMemo(
    () => (title: string) => {
      createTask(repository, title)
        .then(() => refresh())
        .catch((err: Error) => setError(err.message));
    },
    [repository, refresh],
  );

  const handleComplete = useMemo(
    () => (id: string) => {
      completeTask(repository, id)
        .then(() => refresh())
        .catch((err: Error) => setError(err.message));
    },
    [repository, refresh],
  );

  return (
    <section>
      <h1>Task Manager</h1>
      <TaskForm onSubmit={handleCreate} />
      {error && <p role="alert">{error}</p>}
      <ul>
        {tasks.map((task) => (
          <TaskItem key={task.id} task={task} onComplete={handleComplete} />
        ))}
      </ul>
    </section>
  );
}
