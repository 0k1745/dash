import { useState, type FormEvent } from "react";
import { TASK_STATUSES, TASK_STATUS_LABELS, type Task, type TaskStatus } from "../domain/task";

interface TaskItemProps {
  task: Task;
  onChangeStatus: (id: string, status: TaskStatus) => void;
  onAddLabel: (id: string, label: string) => void;
  onRemoveLabel: (id: string, label: string) => void;
}

export function TaskItem({ task, onChangeStatus, onAddLabel, onRemoveLabel }: TaskItemProps) {
  const [newLabel, setNewLabel] = useState("");

  function handleAddLabel(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (newLabel.trim().length === 0) {
      return;
    }
    onAddLabel(task.id, newLabel);
    setNewLabel("");
  }

  return (
    <li>
      <h2>{task.title}</h2>
      <p>{task.description}</p>
      <p>
        {task.startDate} → {task.endDate}
      </p>
      {task.budget != null && <p>Budget: {task.budget}</p>}
      <label>
        Status
        <select
          value={task.status}
          onChange={(event) => onChangeStatus(task.id, event.target.value as TaskStatus)}
        >
          {TASK_STATUSES.map((status) => (
            <option key={status} value={status}>
              {TASK_STATUS_LABELS[status]}
            </option>
          ))}
        </select>
      </label>
      <ul>
        {task.labels.map((label) => (
          <li key={label}>
            {label}
            <button type="button" onClick={() => onRemoveLabel(task.id, label)}>
              Remove
            </button>
          </li>
        ))}
      </ul>
      <form onSubmit={handleAddLabel}>
        <input
          type="text"
          value={newLabel}
          onChange={(event) => setNewLabel(event.target.value)}
          placeholder="New label"
          aria-label={`New label for ${task.title}`}
        />
        <button type="submit">Add label</button>
      </form>
    </li>
  );
}
