import { expect, test } from "@playwright/test";
import type { Task } from "task-manager";

// Integration test: exercises the real shell UI, routing and the
// task-manager feature's application/infrastructure wiring, stubbing only
// the network boundary (the backend REST API) so the test does not depend
// on apps/task-manager being up.
test("navigates to Task Manager from the sidebar and creates a task", async ({ page }) => {
  const tasks: Task[] = [];

  await page.route("**/api/tasks", async (route) => {
    if (route.request().method() === "GET") {
      await route.fulfill({ json: tasks });
      return;
    }
    if (route.request().method() === "POST") {
      const body = route.request().postDataJSON() as { title: string };
      const task: Task = { id: String(tasks.length + 1), title: body.title, completed: false };
      tasks.push(task);
      await route.fulfill({ json: task });
      return;
    }
    await route.continue();
  });

  await page.goto("/");
  await page.getByRole("link", { name: "Task Manager" }).click();
  await expect(page.getByRole("heading", { name: "Task Manager" })).toBeVisible();

  await page.getByLabel("New task title").fill("Write the ADR");
  await page.getByRole("button", { name: "Add task" }).click();

  await expect(page.getByText("Write the ADR")).toBeVisible();
});
