export default function Navbar() {
  const logout = () => {
    localStorage.removeItem("token");
    window.location.href = "/";
  };

  return (
    <div style={styles.nav}>
      <h2 style={{ margin: 0 }}>🚀 Team Task Manager</h2>
      <button onClick={logout} style={styles.btn}>
        Logout
      </button>
    </div>
  );
}

const styles = {
  nav: {
    display: "flex",
    justifyContent: "space-between",
    padding: "15px 20px",
    background: "#1e293b",
    color: "white",
  },
  btn: {
    background: "#ef4444",
    color: "white",
    border: "none",
    padding: "8px 12px",
    cursor: "pointer",
  },
};
