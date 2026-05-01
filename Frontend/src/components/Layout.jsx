import { NavLink, Outlet } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { initials } from "../utils/format";

export default function Layout() {
  const { user, logout, isManager } = useAuth();

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <span className="brand-mark">TT</span>
          <div>
            <strong>Team Task</strong>
            <span>Manager</span>
          </div>
        </div>

        <nav className="nav-list" aria-label="Primary navigation">
          <NavLink to="/dashboard">Dashboard</NavLink>
          <NavLink to="/projects">Projects</NavLink>
          <NavLink to="/tasks">Tasks</NavLink>
          {isManager && <NavLink to="/team">Team</NavLink>}
        </nav>
      </aside>

      <main className="workspace">
        <header className="topbar">
          <div>
            <p className="eyebrow">Workspace</p>
            <h1>Team Task Manager</h1>
          </div>

          <div className="user-menu">
            <div className="avatar">{initials(user?.name)}</div>
            <div>
              <strong>{user?.name}</strong>
              <span>{user?.role}</span>
            </div>
            <button className="ghost-button" type="button" onClick={logout}>
              Logout
            </button>
          </div>
        </header>

        <Outlet />
      </main>
    </div>
  );
}
