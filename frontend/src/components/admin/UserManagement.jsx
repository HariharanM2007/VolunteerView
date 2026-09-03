import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";

import {
  fetchUsers,
  updateUserStatus,
} from "../../store/slices/userSlice";

const UserManagement = () => {
  const dispatch = useDispatch();

  const { items, loading, error } = useSelector(
    (state) => state.users
  );

  useEffect(() => {
    dispatch(fetchUsers());
  }, [dispatch]);

  const handleStatusChange = (id, status) => {
    dispatch(
      updateUserStatus({
        id,
        status,
      })
    );
  };

  return (
    <div className="admin-page">
      <div className="page-header">
        <div>
          <h1>User Management</h1>
          <p>Manage platform users here.</p>
        </div>
      </div>

      {loading && (
        <div className="loading">
          Loading users...
        </div>
      )}

      {error && (
        <div className="error-message">
          {error.message || "Failed to load users."}
        </div>
      )}

      {!loading && !error && items.length === 0 && (
        <div className="empty-state">
          <h3>No Users Found</h3>
          <p>There are no users registered on the platform.</p>
        </div>
      )}

      {!loading && items.length > 0 && (
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>Username</th>
                <th>Email</th>
                <th>Full Name</th>
                <th>Role</th>
                <th>Status</th>
                <th>Organization</th>
                <th>Actions</th>
              </tr>
            </thead>

            <tbody>
              {items.map((user) => (
                <tr key={user.id}>
                  <td>
                    <strong>{user.username}</strong>
                  </td>

                  <td>{user.email}</td>

                  <td>{user.fullName}</td>

                  <td>{user.role}</td>

                  <td>
                    <span className="status-badge">
                      {user.status}
                    </span>
                  </td>

                  <td>
                    {user.organizationName || "-"}
                  </td>

                  <td>
                    <div className="action-buttons">

                      {user.status === "PENDING" && (
                        <>
                          <button
                            className="primary-btn"
                            onClick={() =>
                              handleStatusChange(
                                user.id,
                                "APPROVED"
                              )
                            }
                          >
                            Approve
                          </button>

                          <button
                            className="secondary-btn"
                            onClick={() =>
                              handleStatusChange(
                                user.id,
                                "REJECTED"
                              )
                            }
                          >
                            Reject
                          </button>
                        </>
                      )}

                      {user.status === "APPROVED" && (
                        <button
                          className="danger-btn"
                          onClick={() =>
                            handleStatusChange(
                              user.id,
                              "DEACTIVATED"
                            )
                          }
                        >
                          Deactivate
                        </button>
                      )}

                      {user.status === "REJECTED" && (
                        <button
                          className="primary-btn"
                          onClick={() =>
                            handleStatusChange(
                              user.id,
                              "APPROVED"
                            )
                          }
                        >
                          Approve
                        </button>
                      )}

                      {user.status === "DEACTIVATED" && (
                        <button
                          className="primary-btn"
                          onClick={() =>
                            handleStatusChange(
                              user.id,
                              "APPROVED"
                            )
                          }
                        >
                          Reactivate
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
    </div>
  );
};

export default UserManagement;