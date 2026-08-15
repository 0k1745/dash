import type { Task } from "../domain/task";
import type { TaskRepository } from "../domain/task-repository";

export function createTask(repository: TaskRepository, title: string): Promise<Task> {
  const trimmed = title.trim();
  if (trimmed.length === 0) {
    throw new Error("Task title must not be empty");
  }
  return repository.create(trimmed);
}
