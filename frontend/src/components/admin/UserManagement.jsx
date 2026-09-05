import React, { useState, useEffect } from 'react';
import api from '../../services/api';

const UserManagement = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const response = await api.get('/users');
      setUsers(response.data || []);
    } catch (err) {
      // Error handling
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleStatusChange = async (userId, newStatus) => {
    try {
      await api.patch(`/users/${userId}/status`, { status: newStatus });
      fetchUsers();
    } catch (err) {
      // Error handling
    }
  };

  const handleAssignOrg = async (userId) => {
    const orgId = prompt('Enter Organization ID to assign:');
    if (orgId && !isNaN(orgId)) {
      try {
        await api.patch(`/users/${userId}/assign-organization/${orgId}`);
        fetchUsers();
      } catch (err) {
        // Error handling
      }
    }
  };

  if (loading) {
    return <div className="loading">Loading platform users...</div>;
  }

  return (
    <div className="page-container">
      <div className="page-header">
        <h1>User Management</h1>
      </div>

      <div className="table-responsive">
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
            {users.map((u) => (
              <tr key={u.id}>
                <td>
                  <strong>{u.username}</strong>
                </td>
                <td>{u.fullName}</td>
                <td>{u.email}</td>
                <td>{u.role}</td>
                <td>
                  <span className={`status-badge status-${u.status?.toLowerCase()}`}>
                    {u.status}
                  </span>
                </td>
                <td>{u.organizationName || 'None'}</td>
                <td>
                  <div className="action-buttons">
                    {u.status === 'PENDING' && (
                      <button
                        type="button"
                        className="action-btn approve-btn"
                        onClick={() => handleStatusChange(u.id, 'APPROVED')}
                      >
                        Approve
                      </button>
                    )}
                    {u.status === 'APPROVED' && (
                      <button
                        type="button"
                        className="action-btn reject-btn"
                        onClick={() => handleStatusChange(u.id, 'DEACTIVATED')}
                      >
                        Deactivate
                      </button>
                    )}
                    <button
                      type="button"
                      className="action-btn secondary-btn"
                      onClick={() => handleAssignOrg(u.id)}
                    >
                      Assign Org
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default UserManagement;