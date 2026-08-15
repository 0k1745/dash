import type { Task, TaskStatus } from "../domain/task";
import type { TaskRepository } from "../domain/task-repository";

export function changeTaskStatus(
  repository: TaskRepository,
  id: string,
  status: TaskStatus,
): Promise<Task> {
  return repository.changeStatus(id, status);
}
