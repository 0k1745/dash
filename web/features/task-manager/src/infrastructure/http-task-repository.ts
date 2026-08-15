import type { Task } from "../domain/task";
import type { TaskRepository } from "../domain/task-repository";

// Adapter calling the apps/task-manager REST API.
export class HttpTaskRepository implements TaskRepository {
  private readonly baseUrl: string;

  constructor(baseUrl: string) {
    this.baseUrl = baseUrl;
  }

  async findAll(): Promise<Task[]> {
    const response = await fetch(`${this.baseUrl}/api/tasks`);
    if (!response.ok) {
      throw new Error(`Failed to fetch tasks: ${response.status}`);
    }
    return response.json();
  }

  async create(title: string): Promise<Task> {
    const response = await fetch(`${this.baseUrl}/api/tasks`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ title }),
    });
    if (!response.ok) {
      throw new Error(`Failed to create task: ${response.status}`);
    }
    return response.json();
  }

  async complete(id: string): Promise<Task> {
    const response = await fetch(`${this.baseUrl}/api/tasks/${id}`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ completed: true }),
    });
    if (!response.ok) {
      throw new Error(`Failed to complete task: ${response.status}`);
    }
    return response.json();
  }
}
