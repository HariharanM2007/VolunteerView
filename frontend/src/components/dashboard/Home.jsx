import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import impactReportService from '../../services/impactReportService';
import StatCards from './StatCards';

const Home = () => {
  const user = useSelector((state) => state.auth?.user);
  const enrollments = useSelector(
    (state) => state.enrollments?.items || state.enrollments?.enrollments || []
  );

  const [stats, setStats] = useState(null);
  const [selectedMetric, setSelectedMetric] = useState('volunteers');
  const [activitySearch, setActivitySearch] = useState('');
  const [activeTab, setActiveTab] = useState('overview');

  // Interactive Goal Calculator for Volunteers
  const [targetHours, setTargetHours] = useState(25);

  useEffect(() => {
    let isMounted = true;
    if (user?.role === 'PLATFORM_ADMIN') {
      const statsPromise = impactReportService.getStats();
      if (statsPromise && typeof statsPromise.then === 'function') {
        statsPromise
          .then((data) => {
            if (isMounted) setStats(data);
          })
          .catch(() => {});
      }
    }
    return () => {
      isMounted = false;
    };
  }, [user]);

  const handleExploreClick = () => {
    window.location.href = '/opportunities';
  };

  const filteredEnrollments = enrollments.filter((item) => {
    const term = activitySearch.toLowerCase();
    const volunteer = (item.volunteerName || '').toLowerCase();
    const opp = (item.opportunityTitle || '').toLowerCase();
    return volunteer.includes(term) || opp.includes(term);
  });

  return (
    <div className="dashboard">
      <div className="dashboard-hero-banner">
        <div className="dashboard-welcome">
          <h1>Welcome, {user?.fullName || 'User'}!</h1>
          <p className="user-role-badge">{user?.role}</p>
        </div>
        <div className="dashboard-live-indicator">
          <span className="live-dot" />
          <span>Platform Active</span>
        </div>
      </div>

      {user?.role === 'PLATFORM_ADMIN' ? (
        <div className="admin-dashboard-view">
          {stats && (
            <StatCards
              stats={stats}
              selectedCard={selectedMetric}
              onSelectCard={(metricId) => setSelectedMetric(metricId)}
            />
          )}

          <div className="dashboard-grid">
            {/* Interactive Domain Chart / Metrics Explorer */}
            <div className="dashboard-card domain-chart-card">
              <div className="card-header-flex">
                <div>
                  <h3>Platform Overview</h3>
                  <p className="card-subtitle">
                    Live operational metrics & capacity health
                  </p>
                </div>
                <div className="chart-view-toggle">
                  <button
                    type="button"
                    className={`toggle-btn ${activeTab === 'overview' ? 'active' : ''}`}
                    onClick={() => setActiveTab('overview')}
                  >
                    Metrics
                  </button>
                  <button
                    type="button"
                    className={`toggle-btn ${activeTab === 'breakdown' ? 'active' : ''}`}
                    onClick={() => setActiveTab('breakdown')}
                  >
                    Ratios
                  </button>
                </div>
              </div>

              {activeTab === 'overview' ? (
                <div className="chart-summary">
                  <div className="metric-row-interactive">
                    <span className="metric-label">Total Opportunities</span>
                    <div className="metric-bar-wrap">
                      <div
                        className="metric-fill opp-fill"
                        style={{
                          width: `${Math.min(((stats?.totalOpportunities || 0) / 20) * 100, 100)}%`,
                        }}
                      />
                    </div>
                    <span className="metric-num">
                      {stats?.totalOpportunities || 0}
                    </span>
                  </div>

                  <div className="metric-row-interactive">
                    <span className="metric-label">Total Enrollments</span>
                    <div className="metric-bar-wrap">
                      <div
                        className="metric-fill enroll-fill"
                        style={{
                          width: `${Math.min(((stats?.totalEnrollments || 0) / 50) * 100, 100)}%`,
                        }}
                      />
                    </div>
                    <span className="metric-num">
                      {stats?.totalEnrollments || 0}
                    </span>
                  </div>

                  <div className="metric-row-interactive">
                    <span className="metric-label">Pending Organizations</span>
                    <div className="metric-bar-wrap">
                      <div
                        className="metric-fill pending-fill"
                        style={{
                          width: `${Math.min(((stats?.pendingOrganizations || 0) / 10) * 100, 100)}%`,
                        }}
                      />
                    </div>
                    <span className="metric-num">
                      {stats?.pendingOrganizations || 0}
                    </span>
                  </div>
                </div>
              ) : (
                <div className="ratios-breakdown">
                  <div className="ratio-tile">
                    <span className="ratio-title">Enrollment Velocity</span>
                    <strong>
                      {stats?.totalOpportunities
                        ? (
                            (stats.totalEnrollments || 0) /
                            stats.totalOpportunities
                          ).toFixed(1)
                        : 0}{' '}
                      vols / opp
                    </strong>
                  </div>
                  <div className="ratio-tile">
                    <span className="ratio-title">Approval Queue</span>
                    <strong>
                      {stats?.pendingOrganizations || 0} Orgs awaiting review
                    </strong>
                  </div>
                </div>
              )}
            </div>

            {/* Interactive Recent Activity Feed with Live Filter */}
            <div className="dashboard-card recent-activity-card">
              <div className="card-header-flex">
                <div>
                  <h3>Recent Activity</h3>
                  <p className="card-subtitle">Real-time volunteer events</p>
                </div>
              </div>

              <div className="activity-search-box">
                <input
                  type="text"
                  placeholder="Filter activity log..."
                  value={activitySearch}
                  onChange={(e) => setActivitySearch(e.target.value)}
                  className="activity-search-input"
                />
              </div>

              {filteredEnrollments.length > 0 ? (
                <ul className="activity-list">
                  {filteredEnrollments.slice(0, 5).map((item, idx) => (
                    <li key={item.id || idx} className="activity-item-interactive">
                      <div className="activity-avatar">
                        {(item.volunteerName || 'V').charAt(0)}
                      </div>
                      <div className="activity-content">
                        <span>
                          <strong>{item.volunteerName || 'Volunteer'}</strong>{' '}
                          enrolled in{' '}
                          <strong className="opp-tag">
                            {item.opportunityTitle || 'Opportunity'}
                          </strong>
                        </span>
                        <span className="activity-time">Just now</span>
                      </div>
                    </li>
                  ))}
                </ul>
              ) : (
                <p className="empty-text">No matching enrollment activity.</p>
              )}
            </div>
          </div>
        </div>
      ) : (
        /* Volunteer Interactive Experience Card */
        <div className="user-dashboard-card interactive-journey-card">
          <div className="journey-header">
            <div>
              <h2>Your Volunteering Journey</h2>
              <p>
                Discover meaningful volunteer opportunities in your community,
                connect with non-profit organizations, track your hours, and
                share your impact stories.
              </p>
            </div>
          </div>

          {/* Interactive Milestone Planner */}
          <div className="volunteer-impact-calculator">
            <div className="calculator-header">
              <span className="calc-title">🎯 Set Your Monthly Service Target</span>
              <span className="calc-value">{targetHours} Hours Goal</span>
            </div>
            <input
              type="range"
              min="5"
              max="100"
              step="5"
              value={targetHours}
              onChange={(e) => setTargetHours(e.target.value)}
              className="interactive-slider"
            />
            <div className="calculator-projected">
              <span>Estimated Community Impact:</span>
              <strong>~{Math.round(targetHours * 3.5)} Beneficiaries Supported</strong>
            </div>
          </div>

          <div className="journey-actions">
            <button
              type="button"
              className="primary-btn"
              onClick={handleExploreClick}
            >
              Explore Opportunities
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Home;