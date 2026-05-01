import { useEffect, useState } from "react";
import { API, unwrap } from "../api/axios";
import { formatDate, label } from "../utils/format";

export default function Dashboard() {
  const [dashboard, setDashboard] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    API.get("/dashboard")
      .then((response) => setDashboard(unwrap(response)))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="panel">Loading dashboard...</div>;

  const stats = [
    ["Total Projects", dashboard?.totalProjects ?? 0],
    ["Active Projects", dashboard?.activeProjects ?? 0],
    ["Completed Projects", dashboard?.completedProjects ?? 0],
    ["Total Tasks", dashboard?.totalTasks ?? 0],
    ["My Tasks", dashboard?.myTasks ?? 0],
    ["Overdue", dashboard?.overdueTasksCount ?? 0],
  ];

  return (
    <section className="page-stack">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Overview</p>
          <h2>Delivery snapshot</h2>
        </div>
      </div>

      <div className="stats-grid">
        {stats.map(([name, value]) => (
          <article className="stat-card" key={name}>
            <span>{name}</span>
            <strong>{value}</strong>
          </article>
        ))}
      </div>

      <div className="two-column">
        <section className="panel">
          <div className="panel-title">
            <h3>Tasks by status</h3>
          </div>
          <div className="breakdown">
            {Object.entries(dashboard?.tasksByStatus || {}).map(([status, count]) => (
              <div key={status}>
                <span>{label(status)}</span>
                <strong>{count}</strong>
              </div>
            ))}
          </div>
        </section>

        <section className="panel">
          <div className="panel-title">
            <h3>Priority mix</h3>
          </div>
          <div className="breakdown">
            {Object.entries(dashboard?.tasksByPriority || {}).map(([priority, count]) => (
              <div key={priority}>
                <span>{label(priority)}</span>
                <strong>{count}</strong>
              </div>
            ))}
          </div>
        </section>
      </div>

      <div className="two-column">
        <TaskList title="Recent tasks assigned to me" tasks={dashboard?.recentMyTasks} />
        <TaskList title="Overdue tasks" tasks={dashboard?.overdueTasks} />
      </div>
    </section>
  );
}

function TaskList({ title, tasks = [] }) {
  return (
    <section className="panel">
      <div className="panel-title">
        <h3>{title}</h3>
      </div>
      <div className="compact-list">
        {tasks.length === 0 && <p className="muted">Nothing to show.</p>}
        {tasks.map((task) => (
          <article key={task.id}>
            <div>
              <strong>{task.title}</strong>
              <span>{task.projectTitle || "No project"}</span>
            </div>
            <small>{formatDate(task.deadline)}</small>
          </article>
        ))}
      </div>
    </section>
  );
}
