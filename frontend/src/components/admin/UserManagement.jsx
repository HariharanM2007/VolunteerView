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

      setUsers(data || []);
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

      <p>
        Manage platform users here.
      </p>

      {loading && (
        <p>Loading users...</p>
      )}

      {error && (
        <p>{error}</p>
      )}

      {!loading && !error && (
        <p>
          Users found: {users.length}
        </p>
      )}
    </div>
  );
};

export default UserManagement;