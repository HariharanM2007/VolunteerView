import React from "react";
import {
  Navigate,
  Route,
  Routes,
} from "react-router-dom";

import { useSelector } from "react-redux";

import Login from "./components/Login";
import Register from "./components/Register";
import UserManagement from "./components/admin/UserManagement";
import OrganizationList from "./components/organization/OrganizationList";

const Placeholder = ({ title }) => {
  return (
    <div className="dashboard">
      <h1>{title}</h1>
    </div>
  );
};

const ProtectedRoute = ({ children }) => {
  const { user } = useSelector((state) => state.auth);

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  return children;
};

const PublicRoute = ({ children }) => {
  const { user } = useSelector((state) => state.auth);

  if (user) {
    return <Navigate to="/" replace />;
  }

  return children;
};

const AdminRoute = ({ children }) => {
  const { user } = useSelector((state) => state.auth);

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (user.role !== "PLATFORM_ADMIN") {
    return <Navigate to="/" replace />;
  }

  return children;
};

function App() {
  return (
    <div className="app-container">
      <main className="main-content">
        <Routes>
          <Route
            path="/login"
            element={
              <PublicRoute>
                <Login />
              </PublicRoute>
            }
          />

          <Route
            path="/register"
            element={
              <PublicRoute>
                <Register />
              </PublicRoute>
            }
          />

          <Route
            path="/"
            element={
              <ProtectedRoute>
                <Placeholder title="Dashboard" />
              </ProtectedRoute>
            }
          />

          <Route
            path="/organizations"
            element={
            user ? <OrganizationList /> : <Navigate to="/login" />
            }
          />

          <Route
            path="/opportunities"
            element={
              <ProtectedRoute>
                <Placeholder title="Volunteer Opportunities" />
              </ProtectedRoute>
            }
          />

          <Route
            path="/enrollments"
            element={
              <ProtectedRoute>
                <Placeholder title="Volunteer Enrollments" />
              </ProtectedRoute>
            }
          />

          <Route
            path="/impact-reports"
            element={
              <ProtectedRoute>
                <Placeholder title="Community Impact Reports" />
              </ProtectedRoute>
            }
          />

          <Route
            path="/users"
            element={
              <AdminRoute>
                <UserManagement />
              </AdminRoute>
            }
          />

          <Route
            path="*"
            element={<Navigate to="/" replace />}
          />
        </Routes>
      </main>
    </div>
  );
}

export default App;