import type { Task, TaskStatus } from "./task";

export interface NewTask {
  title: string;
  description: string;
  startDate: string;
  endDate: string;
  budget?: number;
}

// Port implemented by infrastructure adapters (e.g. HttpTaskRepository).
export interface TaskRepository {
  findAll(): Promise<Task[]>;
  searchByLabels(labels: string[]): Promise<Task[]>;
  create(task: NewTask): Promise<Task>;
  changeStatus(id: string, status: TaskStatus): Promise<Task>;
  addLabel(id: string, label: string): Promise<Task>;
  removeLabel(id: string, label: string): Promise<Task>;
  remove(id: string): Promise<void>;
}
