import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import api from '../../services/api';

const UserManagement = () => {
  const currentUser = useSelector((state) => state.auth?.user);
  const currentUserId = currentUser?.userId ?? currentUser?.id;
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const response = await api.get('/users');
      const data = response?.data;

      // Extract array safely from List or Page object
      if (Array.isArray(data)) {
        setUsers(data);
      } else if (Array.isArray(data?.content)) {
        setUsers(data.content);
      } else {
        setUsers([]);
      }
    } catch (err) {
      setUsers([]);
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

  const handleDelete = async (user) => {
    if (!window.confirm(`Delete ${user.username}? This permanently removes the user and their volunteer records.`)) {
      return;
    }

    try {
      await api.delete(`/users/${user.id}`);
      await fetchUsers();
    } catch (err) {
      window.alert(err.response?.data?.message || 'Failed to delete user.');
    }
  };

  if (loading) {
    return <div className="loading">Loading platform users...</div>;
  }

  const userList = Array.isArray(users) ? users : [];

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
            {userList.length === 0 ? (
              <tr>
                <td colSpan="7" style={{ textAlign: 'center', padding: '2rem' }}>
                  No users found.
                </td>
              </tr>
            ) : (
              userList.map((u) => {
                const isCurrentUser = Number(u.id) === Number(currentUserId);

                return (
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
                        {u.status === 'APPROVED' && !isCurrentUser && (
                          <button
                            type="button"
                            className="action-btn reject-btn"
                            onClick={() => handleStatusChange(u.id, 'DEACTIVATED')}
                          >
                            Deactivate
                          </button>
                        )}
                        {u.status === 'APPROVED' && isCurrentUser && (
                          <span className="action-btn secondary-btn" style={{ opacity: 0.7, cursor: 'default' }}>
                            Current Admin
                          </span>
                        )}
                        {u.status === 'DEACTIVATED' && (
                          <button
                            type="button"
                            className="action-btn reject-btn"
                            onClick={() => handleDelete(u)}
                          >
                            Delete User
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
                );
              })
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default UserManagement;