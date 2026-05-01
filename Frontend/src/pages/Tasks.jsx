import { useEffect, useState } from "react";
import { API, unwrap } from "../api/axios";
import TaskCard from "../components/TaskCard";
import { CATEGORIES, PRIORITIES, TASK_STATUSES, fromDateTimeLocal, label } from "../utils/format";
import { useAuth } from "../context/AuthContext";

const emptyTask = {
  title: "",
  description: "",
  status: "TODO",
  priority: "MEDIUM",
  category: "FEATURE",
  projectId: "",
  assigneeId: "",
  reviewerId: "",
  deadline: "",
  fixVersion: "",
};

export default function Tasks() {
  const { isManager } = useAuth();
  const [tasks, setTasks] = useState([]);
  const [projects, setProjects] = useState([]);
  const [users, setUsers] = useState([]);
  const [form, setForm] = useState(emptyTask);
  const [filter, setFilter] = useState("ALL");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  const loadTasks = () => API.get("/tasks").then((response) => setTasks(unwrap(response)));

  useEffect(() => {
    Promise.all([
      loadTasks(),
      API.get("/projects").then((response) => setProjects(unwrap(response))),
      API.get("/users").then((response) => setUsers(unwrap(response))).catch(() => []),
    ]).finally(() => setLoading(false));
  }, []);

  const createTask = async (event) => {
    event.preventDefault();
    setError("");

    try {
      await API.post("/tasks", {
        ...form,
        projectId: Number(form.projectId),
        assigneeId: form.assigneeId ? Number(form.assigneeId) : null,
        reviewerId: form.reviewerId ? Number(form.reviewerId) : null,
        deadline: fromDateTimeLocal(form.deadline),
      });
      setForm(emptyTask);
      await loadTasks();
    } catch (err) {
      setError(err.response?.data?.message || "Could not create task");
    }
  };

  const updateStatus = async (id, status) => {
    await API.patch(`/tasks/${id}/status`, { status });
    await loadTasks();
  };

  const deleteTask = async (id) => {
    await API.delete(`/tasks/${id}`);
    await loadTasks();
  };

  const visibleTasks = filter === "ALL" ? tasks : tasks.filter((task) => task.status === filter);

  if (loading) return <div className="panel">Loading tasks...</div>;

  return (
    <section className="page-stack">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Task assignment & status tracking</p>
          <h2>Tasks</h2>
        </div>
        <select value={filter} onChange={(event) => setFilter(event.target.value)}>
          <option value="ALL">All statuses</option>
          {TASK_STATUSES.map((status) => (
            <option key={status} value={status}>{label(status)}</option>
          ))}
        </select>
      </div>

      {isManager && (
        <form className="panel form-grid" onSubmit={createTask}>
          <div className="form-span">
            <h3>Create task</h3>
            {error && <div className="alert error">{error}</div>}
          </div>

          <label>
            Title
            <input
              value={form.title}
              onChange={(event) => setForm({ ...form, title: event.target.value })}
              required
            />
          </label>

          <label>
            Project
            <select
              value={form.projectId}
              onChange={(event) => setForm({ ...form, projectId: event.target.value })}
              required
            >
              <option value="">Select project</option>
              {projects.map((project) => (
                <option key={project.id} value={project.id}>{project.title}</option>
              ))}
            </select>
          </label>

          <label className="form-span">
            Description
            <textarea
              value={form.description}
              onChange={(event) => setForm({ ...form, description: event.target.value })}
              rows={3}
            />
          </label>

          <label>
            Status
            <select value={form.status} onChange={(event) => setForm({ ...form, status: event.target.value })}>
              {TASK_STATUSES.map((status) => (
                <option key={status} value={status}>{label(status)}</option>
              ))}
            </select>
          </label>

          <label>
            Priority
            <select value={form.priority} onChange={(event) => setForm({ ...form, priority: event.target.value })}>
              {PRIORITIES.map((priority) => (
                <option key={priority} value={priority}>{label(priority)}</option>
              ))}
            </select>
          </label>

          <label>
            Category
            <select value={form.category} onChange={(event) => setForm({ ...form, category: event.target.value })}>
              {CATEGORIES.map((category) => (
                <option key={category} value={category}>{label(category)}</option>
              ))}
            </select>
          </label>

          <label>
            Deadline
            <input
              type="datetime-local"
              value={form.deadline}
              onChange={(event) => setForm({ ...form, deadline: event.target.value })}
            />
          </label>

          <label>
            Assignee
            <select value={form.assigneeId} onChange={(event) => setForm({ ...form, assigneeId: event.target.value })}>
              <option value="">Unassigned</option>
              {users.map((user) => (
                <option key={user.id} value={user.id}>{user.name}</option>
              ))}
            </select>
          </label>

          <label>
            Reviewer
            <select value={form.reviewerId} onChange={(event) => setForm({ ...form, reviewerId: event.target.value })}>
              <option value="">No reviewer</option>
              {users.map((user) => (
                <option key={user.id} value={user.id}>{user.name}</option>
              ))}
            </select>
          </label>

          <label>
            Fix version
            <input
              value={form.fixVersion}
              onChange={(event) => setForm({ ...form, fixVersion: event.target.value })}
              placeholder="v1.0"
            />
          </label>

          <button className="primary-button" type="submit">Create task</button>
        </form>
      )}

      <div className="task-grid">
        {visibleTasks.length === 0 && <div className="panel">No tasks found.</div>}
        {visibleTasks.map((task) => (
          <TaskCard
            key={task.id}
            task={task}
            canManage={isManager}
            onStatusChange={updateStatus}
            onDelete={deleteTask}
          />
        ))}
      </div>
    </section>
  );
}
