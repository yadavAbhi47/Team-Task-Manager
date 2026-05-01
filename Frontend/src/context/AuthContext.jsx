/* eslint-disable react-refresh/only-export-components */
import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { API, unwrap } from "../api/axios";

const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem("user");
    return savedUser ? JSON.parse(savedUser) : null;
  });
  const [loading, setLoading] = useState(() => Boolean(localStorage.getItem("token")));

  const logout = useCallback(() => {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    localStorage.removeItem("user");
    setUser(null);
    setLoading(false);
  }, []);

  useEffect(() => {
    const token = localStorage.getItem("token");

    if (!token) {
      return;
    }

    API.get("/auth/me")
      .then((response) => {
        const currentUser = unwrap(response);
        localStorage.setItem("user", JSON.stringify(currentUser));
        localStorage.setItem("role", currentUser.role);
        setUser(currentUser);
      })
      .catch(() => logout())
      .finally(() => setLoading(false));
  }, [logout]);

  const login = async (email, password) => {
    const response = await API.post("/auth/login", { email, password });
    const auth = unwrap(response);
    const currentUser = {
      id: auth.id,
      name: auth.name,
      email: auth.email,
      role: auth.role,
      profilePicture: auth.profilePicture,
    };

    localStorage.setItem("token", auth.token);
    localStorage.setItem("role", auth.role);
    localStorage.setItem("user", JSON.stringify(currentUser));
    setUser(currentUser);
    return currentUser;
  };

  const signup = async (data) => {
    const response = await API.post("/auth/signup", data);
    return unwrap(response);
  };

  const value = useMemo(
    () => ({
      user,
      loading,
      isAuthenticated: Boolean(user && localStorage.getItem("token")),
      isManager: ["ADMIN", "MANAGER"].includes(user?.role),
      isAdmin: user?.role === "ADMIN",
      login,
      signup,
      logout,
    }),
    [loading, logout, user],
  );

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
