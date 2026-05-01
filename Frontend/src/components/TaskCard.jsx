import { formatDate, label } from "../utils/format";

export default function TaskCard({ task, onStatusChange, onDelete, canManage }) {
  return (
    <article className={`task-card priority-${task.priority?.toLowerCase()}`}>
      <div className="task-card-head">
        <div>
          <h3>{task.title}</h3>
          <span>{task.projectTitle || "No project"}</span>
        </div>
        {task.overdue && <span className="badge danger">Overdue</span>}
      </div>

      {task.description && <p>{task.description}</p>}

      <div className="meta-grid">
        <span>Status <strong>{label(task.status)}</strong></span>
        <span>Priority <strong>{label(task.priority)}</strong></span>
        <span>Assignee <strong>{task.assignee?.name || "Unassigned"}</strong></span>
        <span>Deadline <strong>{formatDate(task.deadline)}</strong></span>
      </div>

      <div className="card-actions">
        <select
          aria-label="Update task status"
          value={task.status}
          onChange={(event) => onStatusChange?.(task.id, event.target.value)}
        >
          {[
            "TODO",
            "IN_PROGRESS",
            "ON_REVIEW",
            "REOPENED",
            "READY_TO_MERGE",
            "MERGED_TO_MASTER",
            "DEV_DEPLOYED",
            "DEV_VERIFIED",
            "STAGE_DEVELOPED",
            "STAGE_VERIFIED",
            "PROD_DEPLOYED",
            "PROD_VERIFIED",
            "DONE",
          ].map((status) => (
            <option key={status} value={status}>{label(status)}</option>
          ))}
        </select>

        {canManage && (
          <button className="danger-button" type="button" onClick={() => onDelete?.(task.id)}>
            Delete
          </button>
        )}
      </div>
    </article>
  );
}
