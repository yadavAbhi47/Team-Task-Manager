import { Link } from "react-router-dom";

export default function Sidebar() {
  return (
    <div style={styles.sidebar}>
      <h3>Menu</h3>

      <Link to="/dashboard" style={styles.link}>Dashboard</Link>
      <Link to="/projects" style={styles.link}>Projects</Link>
      <Link to="/tasks" style={styles.link}>Tasks</Link>
    </div>
  );
}

const styles = {
  sidebar: {
    width: "200px",
    height: "100vh",
    background: "#0f172a",
    color: "white",
    padding: "20px",
    display: "flex",
    flexDirection: "column",
    gap: "10px",
  },
  link: {
    color: "white",
    textDecoration: "none",
  },
};