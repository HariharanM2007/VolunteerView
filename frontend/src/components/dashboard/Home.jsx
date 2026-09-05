import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import impactReportService from '../../services/impactReportService';
import StatCards from './StatCards';

const Home = () => {
  const navigate = useNavigate();
  const user = useSelector((state) => state.auth?.user);
  const enrollments = useSelector(
    (state) => state.enrollments?.items || state.enrollments?.enrollments || []
  );
  const [stats, setStats] = useState(null);

  useEffect(() => {
    if (user?.role === 'PLATFORM_ADMIN') {
      impactReportService
        .getStats()
        .then((data) => setStats(data))
        .catch(() => {});
    }
  }, [user]);

  return (
    <div className="dashboard">
      <div className="dashboard-welcome">
        <h1>Welcome, {user?.fullName || 'User'}!</h1>
        <p className="user-role-badge">{user?.role}</p>
      </div>

      {user?.role === 'PLATFORM_ADMIN' ? (
        <div className="admin-dashboard-view">
          {stats && <StatCards stats={stats} />}
          <div className="dashboard-grid">
            <div className="dashboard-card domain-chart-card">
              <h3>Platform Overview</h3>
              <div className="chart-summary">
                <p>Total Opportunities: {stats?.totalOpportunities || 0}</p>
                <p>Total Enrollments: {stats?.totalEnrollments || 0}</p>
                <p>Pending Organizations: {stats?.pendingOrganizations || 0}</p>
              </div>
            </div>
            <div className="dashboard-card recent-activity-card">
              <h3>Recent Activity</h3>
              {enrollments.length > 0 ? (
                <ul className="activity-list">
                  {enrollments.slice(0, 5).map((item, idx) => (
                    <li key={item.id || idx}>
                      {item.volunteerName || 'Volunteer'} enrolled in{' '}
                      <strong>{item.opportunityTitle || 'Opportunity'}</strong>
                    </li>
                  ))}
                </ul>
              ) : (
                <p className="empty-text">No recent enrollment activity.</p>
              )}
            </div>
          </div>
        </div>
      ) : (
        <div className="user-dashboard-card">
          <h2>Your Volunteering Journey</h2>
          <p>
            Discover meaningful volunteer opportunities in your community, connect
            with non-profit organizations, track your hours, and share your impact stories.
          </p>
          <button
            type="button"
            className="primary-btn"
            onClick={() => navigate('/opportunities')}
          >
            Explore Opportunities
          </button>
        </div>
      )}
    </div>
  );
};

export default Home;