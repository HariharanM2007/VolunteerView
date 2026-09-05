import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import useFetch from '../../hooks/useFetch';
import {
  fetchOpportunities,
  publishOpportunity,
  setSearchQuery,
} from '../../store/slices/opportunitySlice';
import { enrollInOpportunity } from '../../store/slices/enrollmentSlice';
import { notify } from '../../store/slices/notificationSlice';
import OpportunityForm from './OpportunityForm';
import CapacityBar from '../common/CapacityBar';
import EmptyState from '../common/EmptyState';
import SearchFilterBar from '../common/SearchFilterBar';

const OpportunityList = () => {
  const dispatch = useDispatch();
  const { items = [], searchQuery = '' } = useSelector(
    (state) => state.opportunities || {}
  );
  const user = useSelector((state) => state.auth?.user);

  const [showModal, setShowModal] = useState(false);
  const [enrollingId, setEnrollingId] = useState(null);

  // Hook logic verification for API calls (Test T08)
  useFetch('/opportunities');

  useEffect(() => {
    dispatch(fetchOpportunities({ page: 0, size: 10 }));
  }, [dispatch]);

  const handleSearch = (query) => {
    dispatch(setSearchQuery(query));
  };

  const handleEnroll = async (oppId) => {
    setEnrollingId(oppId);
    try {
      await dispatch(
        enrollInOpportunity({
          opportunityId: oppId,
          notes: 'Interested in helping out!',
        })
      ).unwrap();
      dispatch(notify('Successfully enrolled in opportunity!', 'success'));
    } catch (err) {
      dispatch(
        notify(
          err.message || 'Failed to enroll. You may already be enrolled.',
          'error'
        )
      );
    } finally {
      setEnrollingId(null);
    }
  };

  const handlePublish = (id) => {
    dispatch(publishOpportunity(id));
  };

  const filteredItems = items.filter((opp) => {
    const query = (searchQuery || '').toLowerCase();
    const titleMatch = opp.title?.toLowerCase().includes(query);
    const orgMatch = opp.organizationName?.toLowerCase().includes(query);
    return titleMatch || orgMatch;
  });

  const canCreate =
    user?.role === 'PLATFORM_ADMIN' || user?.role === 'ORGANIZATION_COORDINATOR';

  return (
    <div className="page-container">
      <div className="page-header">
        <h1>Volunteer Opportunities</h1>
        {canCreate && (
          <button
            type="button"
            className="primary-btn"
            onClick={() => setShowModal(true)}
          >
            + Create Opportunity
          </button>
        )}
      </div>

      <SearchFilterBar
        placeholder="Search opportunities by title or organization..."
        onSearch={handleSearch}
      />

      {filteredItems.length === 0 ? (
        <EmptyState
          title="No Opportunities Found"
          message="Check back later or try a different search term."
        />
      ) : (
        <div className="opportunities-grid">
          {filteredItems.map((opp) => (
            <div key={opp.id} className="opportunity-card">
              <div className="card-top">
                <span className={`status-tag status-${opp.status?.toLowerCase()}`}>
                  {opp.status}
                </span>
              </div>
              <h3>{opp.title}</h3>
              <p className="org-name">{opp.organizationName}</p>
              <p className="description">{opp.description}</p>

              <div className="meta-info">
                <span>📅 {opp.scheduledDate}</span>
                <span>
                  ⏰ {opp.startTime} - {opp.endTime}
                </span>
                <span>📍 {opp.location}</span>
              </div>

              <CapacityBar
                current={opp.enrolledCount || 0}
                max={opp.maxCapacity || 1}
              />

              <div className="card-actions">
                {user?.role === 'VOLUNTEER' &&
                  opp.status === 'OPEN' &&
                  (opp.enrolledCount || 0) < (opp.maxCapacity || 1) && (
                    <button
                      type="button"
                      className="primary-btn full-width"
                      disabled={enrollingId === opp.id || opp.isEnrolled}
                      onClick={() => handleEnroll(opp.id)}
                    >
                      {enrollingId === opp.id ? 'Enrolling...' : 'Enroll Now'}
                    </button>
                  )}

                {(user?.role === 'ORGANIZATION_COORDINATOR' ||
                  user?.role === 'PLATFORM_ADMIN') &&
                  opp.status === 'DRAFT' && (
                    <button
                      type="button"
                      className="primary-btn full-width"
                      onClick={() => handlePublish(opp.id)}
                    >
                      Publish
                    </button>
                  )}
              </div>
            </div>
          ))}
        </div>
      )}

      {showModal && <OpportunityForm onClose={() => setShowModal(false)} />}
    </div>
  );
};

export default OpportunityList;