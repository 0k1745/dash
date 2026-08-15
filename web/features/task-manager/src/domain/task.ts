export type TaskStatus = "TODO" | "ANALYSIS" | "IN_PROGRESS" | "DONE";

export const TASK_STATUSES: TaskStatus[] = ["TODO", "ANALYSIS", "IN_PROGRESS", "DONE"];

export const TASK_STATUS_LABELS: Record<TaskStatus, string> = {
  TODO: "À faire",
  ANALYSIS: "En cours d'analyse",
  IN_PROGRESS: "En cours",
  DONE: "Terminé",
};

export interface Task {
  id: string;
  title: string;
  description: string;
  startDate: string;
  endDate: string;
  labels: string[];
  status: TaskStatus;
  budget: number | null;
}
