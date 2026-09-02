import React, { useEffect, useState } from "react";

import userService from "../../services/userService";

const UserManagement = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadUsers();
  }, []);

  const loadUsers = async () => {
    try {
      setLoading(true);
      setError("");

      const data = await userService.getAll();

      setUsers(data.content || []);
    } catch (err) {
      console.error("Failed to load users:", err);

      setError(
        err.response?.data?.message ||
          "Failed to load users."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h1>User Management</h1>

      <p>Manage platform users here.</p>

      {loading && <p>Loading users...</p>}

      {error && <p>{error}</p>}

      {!loading && !error && (
        <table>
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
            {users.map((user) => (
              <tr key={user.id}>
                <td>{user.username}</td>
                <td>{user.email}</td>
                <td>{user.fullName}</td>
                <td>{user.role}</td>
                <td>{user.status}</td>
                <td>
                  {user.organizationName || "-"}
                </td>
                <td>
                  <button>Actions</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default UserManagement;