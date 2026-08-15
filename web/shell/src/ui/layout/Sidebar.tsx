import { NavLink } from "react-router-dom";
import { applications } from "../../applications";

export function Sidebar() {
  return (
    <nav aria-label="Applications">
      <ul>
        {applications.map((application) => (
          <li key={application.path}>
            <NavLink to={application.path}>{application.label}</NavLink>
          </li>
        ))}
      </ul>
    </nav>
  );
}
