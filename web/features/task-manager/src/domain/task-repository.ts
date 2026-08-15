import type { Task } from "./task";

// Port implemented by infrastructure adapters (e.g. HttpTaskRepository).
export interface TaskRepository {
  findAll(): Promise<Task[]>;
  create(title: string): Promise<Task>;
  complete(id: string): Promise<Task>;
}
