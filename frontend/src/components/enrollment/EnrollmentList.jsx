import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  fetchEnrollments,
  approveEnrollment,
  rejectEnrollment,
  checkInEnrollment,
  completeEnrollment,
  setFilterStatus,
} from '../../store/slices/enrollmentSlice';
import { notify } from '../../store/slices/notificationSlice';
import EmptyState from '../common/EmptyState';
import SearchFilterBar from '../common/SearchFilterBar';

const EnrollmentList = () => {
  const dispatch = useDispatch();
  const { enrollments = [], filterStatus = 'ALL' } = useSelector(
    (state) => state.enrollments || {}
  );
  const user = useSelector((state) => state.auth?.user);

  useEffect(() => {
    dispatch(fetchEnrollments({ page: 0, size: 10 }));
  }, [dispatch]);

  const handleStatusFilter = (status) => {
    dispatch(setFilterStatus(status));
  };

  const handleApprove = async (id) => {
    try {
      await dispatch(approveEnrollment(id)).unwrap();
      dispatch(notify('Enrollment approved successfully', 'success'));
    } catch (err) {
      dispatch(notify(err.message || 'Failed to approve enrollment', 'error'));
    }
  };

  const handleReject = async (id) => {
    try {
      await dispatch(rejectEnrollment(id)).unwrap();
      dispatch(notify('Enrollment rejected', 'info'));
    } catch (err) {
      dispatch(notify(err.message || 'Failed to reject enrollment', 'error'));
    }
  };

  const handleCheckIn = async (id) => {
    try {
      await dispatch(checkInEnrollment(id)).unwrap();
      dispatch(notify('Volunteer checked in successfully', 'success'));
    } catch (err) {
      dispatch(notify(err.message || 'Failed to check in', 'error'));
    }
  };

  const handleComplete = async (id) => {
    const hours = prompt('Enter completed volunteer hours:');
    if (hours && !isNaN(hours)) {
      try {
        await dispatch(
          completeEnrollment({ id, hours: parseFloat(hours) })
        ).unwrap();
        dispatch(notify('Enrollment marked as completed', 'success'));
      } catch (err) {
        dispatch(notify(err.message || 'Failed to complete enrollment', 'error'));
      }
    }
  };

  const filteredEnrollments = enrollments.filter((e) => {
    if (filterStatus === 'ALL') return true;
    return e.status === filterStatus;
  });

  const filterOptions = [
    { label: 'All Statuses', value: 'ALL' },
    { label: 'Pending', value: 'PENDING' },
    { label: 'Approved', value: 'APPROVED' },
    { label: 'Started', value: 'STARTED' },
    { label: 'Completed', value: 'COMPLETED' },
    { label: 'Rejected', value: 'REJECTED' },
  ];

  return (
    <div className="page-container">
      <div className="page-header">
        <h1>Volunteer Enrollments</h1>
      </div>

      <SearchFilterBar
        placeholder="Filter enrollments..."
        filters={filterOptions}
        onFilter={handleStatusFilter}
      />

      {filteredEnrollments.length === 0 ? (
        <EmptyState
          title="No enrollments found"
          message="There are no enrollments matching the selected criteria."
        />
      ) : (
        <div className="table-responsive">
          <table className="data-table">
            <thead>
              <tr>
                <th>Volunteer</th>
                <th>Opportunity</th>
                <th>Org</th>
                <th>Status</th>
                <th>Hours</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredEnrollments.map((enrollment) => (
                <tr key={enrollment.id}>
                  <td>
                    <strong>{enrollment.volunteerName || 'Volunteer'}</strong>
                  </td>
                  <td>{enrollment.opportunityTitle}</td>
                  <td>{enrollment.organizationName}</td>
                  <td>
                    <span
                      className={`status-badge status-${enrollment.status?.toLowerCase()}`}
                    >
                      {enrollment.status}
                    </span>
                  </td>
                  <td>{enrollment.hoursLogged ?? '-'}</td>
                  <td>
                    {(user?.role === 'PLATFORM_ADMIN' ||
                      user?.role === 'ORGANIZATION_COORDINATOR') && (
                      <>
                        {enrollment.status === 'PENDING' && (
                          <div className="action-buttons">
                            <button
                              type="button"
                              className="action-btn approve-btn"
                              onClick={() => handleApprove(enrollment.id)}
                            >
                              Approve
                            </button>
                            <button
                              type="button"
                              className="action-btn reject-btn"
                              onClick={() => handleReject(enrollment.id)}
                            >
                              Reject
                            </button>
                          </div>
                        )}
                        {enrollment.status === 'APPROVED' && (
                          <button
                            type="button"
                            className="action-btn checkin-btn"
                            onClick={() => handleCheckIn(enrollment.id)}
                          >
                            Manual Check-In
                          </button>
                        )}
                        {enrollment.status === 'STARTED' && (
                          <button
                            type="button"
                            className="action-btn complete-btn"
                            onClick={() => handleComplete(enrollment.id)}
                          >
                            Complete
                          </button>
                        )}
                      </>
                    )}

                    {user?.role === 'VOLUNTEER' &&
                      enrollment.status === 'APPROVED' && (
                        <button
                          type="button"
                          className="action-btn checkin-btn"
                          onClick={() => handleCheckIn(enrollment.id)}
                        >
                          Check In
                        </button>
                      )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default EnrollmentList;