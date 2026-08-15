import type { Task } from "../domain/task";
import type { TaskRepository } from "../domain/task-repository";

export function listTasks(repository: TaskRepository): Promise<Task[]> {
  return repository.findAll();
}
