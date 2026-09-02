import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";

import userService from "../../services/userService";
import organizationService from "../../services/organizationService";

const UserManagement = () => {
  const dispatch = useDispatch();

  const { user } = useSelector((state) => state.auth);

  const [users, setUsers] = useState([]);
  const [organizations, setOrganizations] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [updatingId, setUpdatingId] = useState(null);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      setError("");

      const [userData, organizationData] = await Promise.all([
        userService.getAll(),
        organizationService.getAll(0, 100),
      ]);

      setUsers(userData || []);

      setOrganizations(
        organizationData?.content || organizationData || []
      );
    } catch (err) {
      console.error("Failed to load user management data:", err);

      setError(
        err.response?.data?.message ||
          "Failed to load users. Please try again."
      );
    } finally {
      setLoading(false);
    }
  };

  const handleStatusChange = async (id, status) => {
    try {
      setUpdatingId(id);
      setError("");

      const updatedUser = await userService.updateStatus(id, status);

      setUsers((currentUsers) =>
        currentUsers.map((item) =>
          item.id === id ? updatedUser : item
        )
      );
    } catch (err) {
      console.error("Failed to update user status:", err);

      setError(
        err.response?.data?.message ||
          "Failed to update user status."
      );
    } finally {
      setUpdatingId(null);
    }
  };

  const handleOrganizationChange = async (
    id,
    organizationId
  ) => {
    if (!organizationId) {
      return;
    }

    try {
      setUpdatingId(id);
      setError("");

      const updatedUser =
        await userService.assignOrganization(
          id,
          organizationId
        );

      setUsers((currentUsers) =>
        currentUsers.map((item) =>
          item.id === id ? updatedUser : item
        )
      );
    } catch (err) {
      console.error(
        "Failed to assign organization:",
        err
      );

      setError(
        err.response?.data?.message ||
          "Failed to assign organization."
      );
    } finally {
      setUpdatingId(null);
    }
  };

  if (!user || user.role !== "PLATFORM_ADMIN") {
    return (
      <div className="empty-state">
        <h3>Access Denied</h3>
        <p>
          Only platform administrators can manage users.
        </p>
      </div>
    );
  }

  return (
    <section className="user-management">
      <div className="page-header">
        <div>
          <h1>User Management</h1>

          <p>
            Manage platform users, account status, and
            organization assignments.
          </p>
        </div>

        <button
          className="secondary-btn"
          onClick={loadData}
          disabled={loading}
        >
          {loading ? "Loading..." : "Refresh"}
        </button>
      </div>

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      {loading ? (
        <div className="loading-state">
          Loading users...
        </div>
      ) : users.length === 0 ? (
        <div className="empty-state">
          <div className="empty-state-icon">👥</div>

          <h3>No Users Found</h3>

          <p>
            There are currently no users to manage.
          </p>
        </div>
      ) : (
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>Username</th>
                <th>Full Name</th>
                <th>Email</th>
                <th>Role</th>
                <th>Status</th>
                <th>Organization</th>
                <th>Actions</th>
              </tr>
            </thead>

            <tbody>
              {users.map((item) => (
                <tr key={item.id}>
                  <td>
                    <strong>{item.username}</strong>
                  </td>

                  <td>{item.fullName}</td>

                  <td>{item.email}</td>

                  <td>
                    <span className="role-badge">
                      {formatRole(item.role)}
                    </span>
                  </td>

                  <td>
                    <span
                      className={`status-badge ${String(
                        item.status
                      ).toLowerCase()}`}
                    >
                      {item.status}
                    </span>
                  </td>

                  <td>
                    <select
                      value={item.organizationId || ""}
                      onChange={(e) =>
                        handleOrganizationChange(
                          item.id,
                          e.target.value
                        )
                      }
                      disabled={updatingId === item.id}
                    >
                      <option value="">
                        No Organization
                      </option>

                      {organizations.map((organization) => (
                        <option
                          key={organization.id}
                          value={organization.id}
                        >
                          {organization.name}
                        </option>
                      ))}
                    </select>
                  </td>

                  <td>
                    <div className="action-buttons">
                      {item.status === "PENDING" && (
                        <>
                          <button
                            className="success-btn"
                            onClick={() =>
                              handleStatusChange(
                                item.id,
                                "APPROVED"
                              )
                            }
                            disabled={
                              updatingId === item.id
                            }
                          >
                            Approve
                          </button>

                          <button
                            className="danger-btn"
                            onClick={() =>
                              handleStatusChange(
                                item.id,
                                "REJECTED"
                              )
                            }
                            disabled={
                              updatingId === item.id
                            }
                          >
                            Reject
                          </button>
                        </>
                      )}

                      {item.status === "APPROVED" && (
                        <button
                          className="danger-btn"
                          onClick={() =>
                            handleStatusChange(
                              item.id,
                              "DEACTIVATED"
                            )
                          }
                          disabled={
                            updatingId === item.id
                          }
                        >
                          Deactivate
                        </button>
                      )}

                      {item.status === "REJECTED" && (
                        <button
                          className="success-btn"
                          onClick={() =>
                            handleStatusChange(
                              item.id,
                              "APPROVED"
                            )
                          }
                          disabled={
                            updatingId === item.id
                          }
                        >
                          Approve
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
};

const formatRole = (role) => {
  if (!role) {
    return "-";
  }

  return role
    .replaceAll("_", " ")
    .toLowerCase()
    .replace(/\b\w/g, (char) => char.toUpperCase());
};

export default UserManagement;