import type { ReactElement } from "react";
import {
  HttpTaskRepository,
  createTaskManagerElement,
  taskManagerRoute,
} from "task-manager";

// Composition root: this is the single place that knows how to configure
// each feature's infrastructure adapters (e.g. backend base URLs) and
// registers the applications shown in the sidebar.
const taskManagerRepository = new HttpTaskRepository(
  import.meta.env.VITE_TASK_MANAGER_API_URL ?? "http://localhost:8080",
);

export interface Application {
  path: string;
  label: string;
  element: ReactElement;
}

export const applications: Application[] = [
  {
    path: taskManagerRoute.path,
    label: taskManagerRoute.label,
    element: createTaskManagerElement(taskManagerRepository),
  },
];
