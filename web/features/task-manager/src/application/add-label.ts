import type { Task } from "../domain/task";
import type { TaskRepository } from "../domain/task-repository";

export function addLabel(repository: TaskRepository, id: string, label: string): Promise<Task> {
  const trimmed = label.trim();
  if (trimmed.length === 0) {
    throw new Error("Label must not be empty");
  }
  return repository.addLabel(id, trimmed);
}
