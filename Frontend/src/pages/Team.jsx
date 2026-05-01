import { useEffect, useState } from "react";
import { API, unwrap } from "../api/axios";
import { useAuth } from "../context/AuthContext";
import { formatDate, initials, label } from "../utils/format";

const ASSIGNABLE_ROLES = ["EMPLOYEE", "CLIENT", "MANAGER"];

export default function Team() {
  const { isAdmin, isManager } = useAuth();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [savingUserId, setSavingUserId] = useState(null);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    API.get("/users")
      .then((response) => setUsers(unwrap(response)))
      .finally(() => setLoading(false));
  }, []);

  const updateRole = async (userId, role) => {
    setError("");
    setMessage("");
    setSavingUserId(userId);

    try {
      const response = await API.put(`/users/${userId}`, { role });
      const updatedUser = unwrap(response);

      setUsers((currentUsers) =>
        currentUsers.map((user) => (user.id === userId ? updatedUser : user)),
      );
      setMessage(`${updatedUser.name} is now ${label(updatedUser.role)}.`);
    } catch (err) {
      setError(err.response?.data?.message || "Only admins can change user roles");
    } finally {
      setSavingUserId(null);
    }
  };

  if (!isManager) {
    return <div className="panel">You do not have access to team management.</div>;
  }

  if (loading) return <div className="panel">Loading team...</div>;

  return (
    <section className="page-stack">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Role-based access</p>
          <h2>Team members</h2>
        </div>
      </div>

      {message && <div className="alert success">{message}</div>}
      {error && <div className="alert error">{error}</div>}

      <div className="team-list">
        {users.map((user) => (
          <article className="team-row" key={user.id}>
            <div className="avatar">{initials(user.name)}</div>
            <div>
              <strong>{user.name}</strong>
              <span>{user.email}</span>
            </div>
            {isAdmin ? (
              <label className="role-control">
                Role
                <select
                  value={ASSIGNABLE_ROLES.includes(user.role) ? user.role : ""}
                  onChange={(event) => updateRole(user.id, event.target.value)}
                  disabled={savingUserId === user.id}
                >
                  {!ASSIGNABLE_ROLES.includes(user.role) && (
                    <option value="">{label(user.role)}</option>
                  )}
                  {ASSIGNABLE_ROLES.map((role) => (
                    <option key={role} value={role}>
                      {label(role)}
                    </option>
                  ))}
                </select>
              </label>
            ) : (
              <span className="badge">{label(user.role)}</span>
            )}
            <small>Joined {formatDate(user.createdAt)}</small>
          </article>
        ))}
      </div>
    </section>
  );
}
