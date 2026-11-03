import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { useSelector } from 'react-redux';

// Layout & Common
import Navbar from './components/layout/Navbar';
import NotificationStack from './components/NotificationStack';

// Auth Components
import Login from './components/Login';
import Register from './components/Register';

// Views
import Home from './components/dashboard/Home';
import OrganizationList from './components/organization/OrganizationList';
import OpportunityList from './components/opportunity/OpportunityList';
import EnrollmentList from './components/enrollment/EnrollmentList';
import ImpactReportList from './components/impact-report/ImpactReportList';
import UserManagement from './components/admin/UserManagement';

const AppContent = () => {
  const user = useSelector((state) => state.auth?.user);

  return (
    <div className={`app-container ${user ? 'user-app' : 'auth-app'}`}>
      {user && <Navbar />}
      <NotificationStack />

      <main className="main-content">
        <Routes>
          {/* Public Routes */}
          <Route
            path="/login"
            element={user ? <Navigate to="/" replace /> : <Login />}
          />
          <Route
            path="/register"
            element={user ? <Navigate to="/" replace /> : <Register />}
          />

          {/* Protected Routes */}
          <Route
            path="/"
            element={user ? <Home /> : <Navigate to="/login" replace />}
          />
          <Route
            path="/organizations"
            element={user ? <OrganizationList /> : <Navigate to="/login" replace />}
          />
          <Route
            path="/opportunities"
            element={user ? <OpportunityList /> : <Navigate to="/login" replace />}
          />
          <Route
            path="/enrollments"
            element={user ? <EnrollmentList /> : <Navigate to="/login" replace />}
          />
          <Route
            path="/impact-reports"
            element={user ? <ImpactReportList /> : <Navigate to="/login" replace />}
          />

          {/* Admin Protected Route */}
          <Route
            path="/users"
            element={
              user ? (
                user.role === 'PLATFORM_ADMIN' ? (
                  <UserManagement />
                ) : (
                  <Navigate to="/" replace />
                )
              ) : (
                <Navigate to="/login" replace />
              )
            }
          />

          {/* Fallback */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
    </div>
  );
};

const App = () => {
  return (
    <BrowserRouter>
      <AppContent />
    </BrowserRouter>
  );
};

export default App;