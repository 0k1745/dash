import type { Task, TaskStatus } from "../domain/task";
import type { NewTask, TaskRepository } from "../domain/task-repository";

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

  async searchByLabels(labels: string[]): Promise<Task[]> {
    const query = new URLSearchParams();
    query.set("labels", labels.join(","));
    const response = await fetch(`${this.baseUrl}/api/tasks?${query.toString()}`);
    if (!response.ok) {
      throw new Error(`Failed to search tasks: ${response.status}`);
    }
    return response.json();
  }

  async create(task: NewTask): Promise<Task> {
    const response = await fetch(`${this.baseUrl}/api/tasks`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(task),
    });
    if (!response.ok) {
      throw new Error(`Failed to create task: ${response.status}`);
    }
    return response.json();
  }

  async changeStatus(id: string, status: TaskStatus): Promise<Task> {
    const response = await fetch(`${this.baseUrl}/api/tasks/${id}/status`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ status }),
    });
    if (!response.ok) {
      throw new Error(`Failed to change task status: ${response.status}`);
    }
    return response.json();
  }

  async addLabel(id: string, label: string): Promise<Task> {
    const response = await fetch(`${this.baseUrl}/api/tasks/${id}/labels`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ label }),
    });
    if (!response.ok) {
      throw new Error(`Failed to add label: ${response.status}`);
    }
    return response.json();
  }

  async removeLabel(id: string, label: string): Promise<Task> {
    const response = await fetch(
      `${this.baseUrl}/api/tasks/${id}/labels/${encodeURIComponent(label)}`,
      { method: "DELETE" },
    );
    if (!response.ok) {
      throw new Error(`Failed to remove label: ${response.status}`);
    }
    return response.json();
  }

  async remove(id: string): Promise<void> {
    const response = await fetch(`${this.baseUrl}/api/tasks/${id}`, { method: "DELETE" });
    if (!response.ok) {
      throw new Error(`Failed to delete task: ${response.status}`);
    }
  }
}
