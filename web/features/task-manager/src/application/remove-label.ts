import type { Task } from "../domain/task";
import type { TaskRepository } from "../domain/task-repository";

export function removeLabel(repository: TaskRepository, id: string, label: string): Promise<Task> {
  return repository.removeLabel(id, label);
}
