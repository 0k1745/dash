import type { Task } from "../domain/task";
import type { TaskRepository } from "../domain/task-repository";

// AND semantics: a task is only returned if it carries every requested label.
export function searchTasksByLabels(repository: TaskRepository, labels: string[]): Promise<Task[]> {
  return repository.searchByLabels(labels);
}
