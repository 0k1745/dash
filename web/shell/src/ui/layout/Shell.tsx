import type { ReactNode } from "react";
import { Sidebar } from "./Sidebar";

interface ShellProps {
  children: ReactNode;
}

export function Shell({ children }: ShellProps) {
  return (
    <div className="shell">
      <Sidebar />
      <main className="shell-content">{children}</main>
    </div>
  );
}
