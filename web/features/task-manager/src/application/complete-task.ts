import type { Task } from "../domain/task";
import type { TaskRepository } from "../domain/task-repository";

export function completeTask(repository: TaskRepository, id: string): Promise<Task> {
  return repository.complete(id);
}
