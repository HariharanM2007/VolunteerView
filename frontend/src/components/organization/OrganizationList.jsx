import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  fetchOrganizations,
  approveOrganization,
  deleteOrganization,
} from '../../store/slices/organizationSlice';
import { notify } from '../../store/slices/notificationSlice';
import OrganizationForm from './OrganizationForm';
import EmptyState from '../common/EmptyState';

const OrganizationList = () => {
  const dispatch = useDispatch();
  const { items = [], loading } = useSelector((state) => state.organizations || {});
  const user = useSelector((state) => state.auth?.user);
  const [showModal, setShowModal] = useState(false);

  useEffect(() => {
    dispatch(fetchOrganizations({ page: 0, size: 10 }));
  }, [dispatch]);

  const handleApprove = async (id) => {
    try {
      await dispatch(approveOrganization(id)).unwrap();
      dispatch(notify('Organization approved.', 'success'));
    } catch (err) {
      dispatch(notify(err.message || 'Failed to approve organization.', 'error'));
    }
  };

  const handleDelete = async (org) => {
    if (!window.confirm(`Delete ${org.name}? This also removes its opportunities and enrollments.`)) {
      return;
    }
    try {
      await dispatch(deleteOrganization(org.id)).unwrap();
      dispatch(notify('Organization deleted.', 'success'));
    } catch (err) {
      dispatch(notify(err.message || 'Failed to delete organization.', 'error'));
    }
  };

  const canAdd =
    user?.role === 'PLATFORM_ADMIN' || user?.role === 'ORGANIZATION_COORDINATOR';

  if (loading && items.length === 0) {
    return <div className="loading">Loading organizations...</div>;
  }

  return (
    <div className="page-container">
      <div className="page-header">
        <h1>Organizations</h1>
        {canAdd && (
          <button
            type="button"
            className="primary-btn"
            onClick={() => setShowModal(true)}
          >
            + Add Organization
          </button>
        )}
      </div>

      {items.length === 0 ? (
        <EmptyState
          title="No Organizations Found"
          message="Start by adding a non-profit organization to the platform."
          actionText={user?.role === 'PLATFORM_ADMIN' ? 'Add Organization' : null}
          onAction={user?.role === 'PLATFORM_ADMIN' ? () => setShowModal(true) : null}
        />
      ) : (
        <div className="table-responsive">
          <table className="data-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Mission</th>
                <th>Contact</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {items.map((org) => (
                <tr key={org.id}>
                  <td>
                    <strong>{org.name}</strong>
                  </td>
                  <td>
                    {org.mission && org.mission.length > 80
                      ? `${org.mission.substring(0, 80)}...`
                      : org.mission}
                  </td>
                  <td>{org.contactEmail}</td>
                  <td>
                    <span className={`status-badge status-${org.status?.toLowerCase()}`}>
                      {org.status}
                    </span>
                  </td>
                  <td>
                    {user?.role === 'PLATFORM_ADMIN' && org.status === 'PENDING' && (
                      <button
                        type="button"
                        className="action-btn approve-btn"
                        onClick={() => handleApprove(org.id)}
                      >
                        Approve
                      </button>
                    )}
                    {user?.role === 'PLATFORM_ADMIN' && (
                      <button
                        type="button"
                        className="action-btn delete-btn"
                        onClick={() => handleDelete(org)}
                      >
                        Delete
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {showModal && <OrganizationForm onClose={() => setShowModal(false)} />}
    </div>
  );
};

export default OrganizationList;