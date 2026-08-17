import { useState, type FormEvent } from "react";
import type { NewTask } from "../domain/task-repository";

interface TaskFormProps {
  onSubmit: (task: NewTask) => void;
}

export function TaskForm({ onSubmit }: TaskFormProps) {
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [budget, setBudget] = useState("");

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (
      title.trim().length === 0 ||
      description.trim().length === 0 ||
      startDate.length === 0 ||
      endDate.length === 0
    ) {
      return;
    }
    onSubmit({
      title,
      description,
      startDate,
      endDate,
      budget: budget.trim().length === 0 ? undefined : Number(budget),
    });
    setTitle("");
    setDescription("");
    setStartDate("");
    setEndDate("");
    setBudget("");
  }

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        value={title}
        onChange={(event) => setTitle(event.target.value)}
        placeholder="Title"
        aria-label="New task title"
      />
      <textarea
        value={description}
        onChange={(event) => setDescription(event.target.value)}
        placeholder="Description (markdown)"
        aria-label="New task description"
      />
      <input
        type="date"
        value={startDate}
        onChange={(event) => setStartDate(event.target.value)}
        aria-label="Start date"
      />
      <input
        type="date"
        value={endDate}
        onChange={(event) => setEndDate(event.target.value)}
        aria-label="End date"
      />
      <input
        type="number"
        value={budget}
        onChange={(event) => setBudget(event.target.value)}
        placeholder="Budget (optional)"
        aria-label="Budget"
      />
      <button type="submit">Add task</button>
    </form>
  );
}
