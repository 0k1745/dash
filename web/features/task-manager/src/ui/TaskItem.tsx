import type { Task } from "../domain/task";

interface TaskItemProps {
  task: Task;
  onComplete: (id: string) => void;
}

export function TaskItem({ task, onComplete }: TaskItemProps) {
  return (
    <li>
      <label>
        <input
          type="checkbox"
          checked={task.completed}
          disabled={task.completed}
          onChange={() => onComplete(task.id)}
        />
        <span style={{ textDecoration: task.completed ? "line-through" : "none" }}>
          {task.title}
        </span>
      </label>
    </li>
  );
}
