import { BrowserRouter, Route, Routes } from "react-router-dom";
import { Shell } from "./ui/layout/Shell";
import { applications } from "./applications";

export function App() {
  return (
    <BrowserRouter>
      <Shell>
        <Routes>
          <Route path="/" element={applications[0].element} />
          {applications.map((application) => (
            <Route key={application.path} path={application.path} element={application.element} />
          ))}
        </Routes>
      </Shell>
    </BrowserRouter>
  );
}
