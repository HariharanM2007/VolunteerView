import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";

import {
  fetchOrganizations,
  approveOrganization,
} from "../../store/slices/organizationSlice";

import OrganizationForm from "./OrganizationForm";

const OrganizationList = () => {
  const dispatch = useDispatch();

  const { user } = useSelector((state) => state.auth);

  const {
    items,
    loading,
    error,
  } = useSelector((state) => state.organizations);

  const [showForm, setShowForm] = useState(false);

  useEffect(() => {
    dispatch(fetchOrganizations({ page: 0, size: 10 }));
  }, [dispatch]);

  const handleApprove = (id) => {
    dispatch(approveOrganization(id));
  };

  const canCreate =
    user?.role === "PLATFORM_ADMIN" ||
    user?.role === "ORGANIZATION_COORDINATOR";

  const isAdmin = user?.role === "PLATFORM_ADMIN";

  if (loading) {
    return (
      <div className="loading">
        Loading organizations...
      </div>
    );
  }

  return (
    <div className="page-container">
      <div className="page-header">
        <div>
          <h1>Organizations</h1>
          <p>Manage non-profit organizations on the platform.</p>
        </div>

        {canCreate && (
          <button
            className="primary-btn"
            onClick={() => setShowForm(true)}
          >
            + Add Organization
          </button>
        )}
      </div>

      {error && (
        <div className="error-message">
          {error.message || "Failed to load organizations."}
        </div>
      )}

      {items.length === 0 ? (
        <div className="empty-state">
          <h3>No Organizations Found</h3>

          <p>
            Start by adding a non-profit organization to the platform.
          </p>

          {isAdmin && (
            <button
              className="primary-btn"
              onClick={() => setShowForm(true)}
            >
              Add Organization
            </button>
          )}
        </div>
      ) : (
        <div className="table-container">
          <table>
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
                    {org.mission?.length > 80
                      ? `${org.mission.substring(0, 80)}...`
                      : org.mission}
                  </td>

                  <td>{org.contactEmail}</td>

                  <td>
                    <span className="status-badge">
                      {org.status}
                    </span>
                  </td>

                  <td>
                    {isAdmin && org.status === "PENDING" && (
                      <button
                        className="primary-btn"
                        onClick={() => handleApprove(org.id)}
                      >
                        Approve
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {showForm && (
        <OrganizationForm
          onClose={() => {
            setShowForm(false);
            dispatch(fetchOrganizations({ page: 0, size: 10 }));
          }}
        />
      )}
    </div>
  );
};

export default OrganizationList;