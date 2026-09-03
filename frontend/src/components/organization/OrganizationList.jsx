import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";

import {
  fetchOrganizations,
  approveOrganization,
} from "../../store/slices/organizationSlice";

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
    dispatch(
      fetchOrganizations({
        page: 0,
        size: 10,
      })
    );
  }, [dispatch]);

  const canCreate =
    user?.role === "PLATFORM_ADMIN" ||
    user?.role === "ORGANIZATION_COORDINATOR";

  const isAdmin = user?.role === "PLATFORM_ADMIN";

  const handleApprove = (id) => {
    dispatch(approveOrganization(id));
  };

  if (loading && items.length === 0) {
    return (
      <div className="organizations-page">
        <div className="loading">
          Loading organizations...
        </div>
      </div>
    );
  }

  return (
    <div className="organizations-page">

      <div className="page-header">
        <div>
          <h1>Organizations</h1>
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
          <div className="empty-state-icon">
            🏢
          </div>

          <h3>No Organizations Found</h3>

          <p>
            Start by adding a non-profit organization
            to the platform.
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
                    <span className="text-truncate">
                      {org.mission}
                    </span>
                  </td>

                  <td>
                    {org.contactEmail}
                  </td>

                  <td>
                    <span
                      className={`status-badge ${org.status?.toLowerCase()}`}
                    >
                      {org.status}
                    </span>
                  </td>

                  <td>
                    {isAdmin &&
                      org.status === "PENDING" && (
                        <button
                          className="secondary-btn"
                          onClick={() =>
                            handleApprove(org.id)
                          }
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

    </div>
  );
};

export default OrganizationList;