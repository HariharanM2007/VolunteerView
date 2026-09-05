import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchImpactReports } from '../../store/slices/impactReportSlice';
import ImpactReportForm from './ImpactReportForm';
import EmptyState from '../common/EmptyState';

const ImpactReportList = () => {
  const dispatch = useDispatch();
  const { items = [] } = useSelector((state) => state.impactReports || {});
  const user = useSelector((state) => state.auth?.user);
  const [showModal, setShowModal] = useState(false);

  useEffect(() => {
    dispatch(fetchImpactReports({ page: 0, size: 10 }));
  }, [dispatch]);

  return (
    <div className="page-container">
      <div className="page-header">
        <h1>Community Impact Reports</h1>
        {user?.role === 'VOLUNTEER' && (
          <button
            type="button"
            className="primary-btn"
            onClick={() => setShowModal(true)}
          >
            + Submit Impact Report
          </button>
        )}
      </div>

      {items.length === 0 ? (
        <EmptyState
          title="No Impact Reports Yet"
          message="Once volunteers complete their service, they can share their impact stories here."
          actionText={user?.role === 'VOLUNTEER' ? 'Submit Your First Report' : null}
          onAction={user?.role === 'VOLUNTEER' ? () => setShowModal(true) : null}
        />
      ) : (
        <div className="impact-grid">
          {items.map((report) => (
            <div key={report.id} className="impact-card">
              <div className="impact-card-header">
                <div className="avatar">
                  {report.volunteerName ? report.volunteerName.charAt(0) : 'V'}
                </div>
                <div>
                  <h4>{report.volunteerName}</h4>
                  <small>
                    {report.organizationName} • {report.opportunityTitle}
                  </small>
                </div>
              </div>

              <div className="star-rating">
                {'★'.repeat(report.rating || 5)}
              </div>

              <p className="impact-summary">"{report.summary}"</p>

              <div className="impact-stats-row">
                <div className="impact-stat">
                  <span className="impact-stat-value">
                    {report.hoursContributed}
                  </span>
                  <span className="impact-stat-label">Hours</span>
                </div>
                <div className="impact-stat">
                  <span className="impact-stat-value">
                    {report.beneficiariesServed}
                  </span>
                  <span className="impact-stat-label">Served</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {showModal && <ImpactReportForm onClose={() => setShowModal(false)} />}
    </div>
  );
};

export default ImpactReportList;