import { useEffect, useState } from "react";
import { API, unwrap } from "../api/axios";
import { PROJECT_STATUSES, formatDate, fromDateTimeLocal, label } from "../utils/format";

const emptyForm = {
  title: "",
  description: "",
  status: "TODO",
  managerId: "",
  startTime: "",
  expectedEndTime: "",
  memberIds: [],
};

export default function Projects() {
  const [projects, setProjects] = useState([]);
  const [users, setUsers] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  const loadProjects = () =>
    API.get("/projects").then((response) => setProjects(unwrap(response)));

  useEffect(() => {
    Promise.all([
      loadProjects(),
      API.get("/users").then((response) => setUsers(unwrap(response))).catch(() => []),
    ]).finally(() => setLoading(false));
  }, []);

  const createProject = async (event) => {
    event.preventDefault();
    setError("");

    try {
      await API.post("/projects", {
        ...form,
        managerId: form.managerId ? Number(form.managerId) : null,
        memberIds: form.memberIds.map(Number),
        startTime: fromDateTimeLocal(form.startTime),
        expectedEndTime: fromDateTimeLocal(form.expectedEndTime),
      });
      setForm(emptyForm);
      await loadProjects();
    } catch (err) {
      setError(err.response?.data?.message || "Could not create project");
    }
  };

  const deleteProject = async (id) => {
    await API.delete(`/projects/${id}`);
    await loadProjects();
  };

  if (loading) return <div className="panel">Loading projects...</div>;

  return (
    <section className="page-stack">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Project & team management</p>
          <h2>Projects</h2>
        </div>
      </div>

      <form className="panel form-grid" onSubmit={createProject}>
        <div className="form-span">
          <h3>Create project</h3>
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
          Status
          <select
            value={form.status}
            onChange={(event) => setForm({ ...form, status: event.target.value })}
          >
            {PROJECT_STATUSES.map((status) => (
              <option key={status} value={status}>{label(status)}</option>
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
          Manager
          <select
            value={form.managerId}
            onChange={(event) => setForm({ ...form, managerId: event.target.value })}
          >
            <option value="">Select manager</option>
            {users.map((user) => (
              <option key={user.id} value={user.id}>{user.name} ({label(user.role)})</option>
            ))}
          </select>
        </label>

        <label>
          Members
          <select
            multiple
            value={form.memberIds}
            onChange={(event) =>
              setForm({
                ...form,
                memberIds: Array.from(event.target.selectedOptions, (option) => option.value),
              })
            }
          >
            {users.map((user) => (
              <option key={user.id} value={user.id}>{user.name}</option>
            ))}
          </select>
        </label>

        <label>
          Start time
          <input
            type="datetime-local"
            value={form.startTime}
            onChange={(event) => setForm({ ...form, startTime: event.target.value })}
          />
        </label>

        <label>
          Expected end
          <input
            type="datetime-local"
            value={form.expectedEndTime}
            onChange={(event) => setForm({ ...form, expectedEndTime: event.target.value })}
          />
        </label>

        <button className="primary-button" type="submit">Create project</button>
      </form>

      <div className="card-grid">
        {projects.length === 0 && <div className="panel">No projects found.</div>}
        {projects.map((project) => (
          <article className="project-card" key={project.id}>
            <div className="task-card-head">
              <div>
                <h3>{project.title}</h3>
                <span>{project.manager?.name || "No manager"}</span>
              </div>
              <span className="badge">{label(project.status)}</span>
            </div>
            <p>{project.description || "No description added."}</p>
            <div className="meta-grid">
              <span>Owner <strong>{project.owner?.name || "Unknown"}</strong></span>
              <span>Members <strong>{project.members?.length || 0}</strong></span>
              <span>Start <strong>{formatDate(project.startTime)}</strong></span>
              <span>Due <strong>{formatDate(project.expectedEndTime)}</strong></span>
            </div>
            <div className="member-row">
              {(project.members || []).slice(0, 5).map((member) => (
                <span key={member.id}>{member.name}</span>
              ))}
            </div>
            <button className="danger-button" type="button" onClick={() => deleteProject(project.id)}>
              Delete
            </button>
          </article>
        ))}
      </div>
    </section>
  );
}
