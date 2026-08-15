import type { Task } from "../domain/task";
import type { NewTask, TaskRepository } from "../domain/task-repository";

export function createTask(repository: TaskRepository, task: NewTask): Promise<Task> {
  const title = task.title.trim();
  if (title.length === 0) {
    throw new Error("Task title must not be empty");
  }
  const description = task.description.trim();
  if (description.length === 0) {
    throw new Error("Task description must not be empty");
  }
  if (task.endDate < task.startDate) {
    throw new Error("Task end date must not be before its start date");
  }
  return repository.create({ ...task, title, description });
}
