import type { TaskRepository } from "../domain/task-repository";
import { TaskListPage } from "./TaskListPage";

export const taskManagerRoute = {
  path: "/tasks",
  label: "Task Manager",
};

export function createTaskManagerElement(repository: TaskRepository) {
  return <TaskListPage repository={repository} />;
}

export { HttpTaskRepository } from "../infrastructure/http-task-repository";
export type { Task, TaskStatus } from "../domain/task";
export type { NewTask, TaskRepository } from "../domain/task-repository";
