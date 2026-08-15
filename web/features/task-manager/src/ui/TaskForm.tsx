import { useState, type FormEvent } from "react";

interface TaskFormProps {
  onSubmit: (title: string) => void;
}

export function TaskForm({ onSubmit }: TaskFormProps) {
  const [title, setTitle] = useState("");

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (title.trim().length === 0) {
      return;
    }
    onSubmit(title);
    setTitle("");
  }

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        value={title}
        onChange={(event) => setTitle(event.target.value)}
        placeholder="New task"
        aria-label="New task title"
      />
      <button type="submit">Add task</button>
    </form>
  );
}
