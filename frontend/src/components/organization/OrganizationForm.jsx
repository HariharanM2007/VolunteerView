import React, { useState } from "react";
import { useDispatch } from "react-redux";

import { createOrganization } from "../../store/slices/organizationSlice";

const OrganizationForm = ({ onClose }) => {
  const dispatch = useDispatch();

  const [formData, setFormData] = useState({
    name: "",
    mission: "",
    address: "",
    contactEmail: "",
    contactPhone: "",
    foundedYear: "",
    coordinatorId: "",
  });

  const [error, setError] = useState(null);

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    setError(null);

    const payload = {
      ...formData,
      coordinatorId: Number(formData.coordinatorId),
      foundedYear: formData.foundedYear
        ? Number(formData.foundedYear)
        : null,
      contactPhone: formData.contactPhone || null,
    };

    const result = await dispatch(
      createOrganization(payload)
    );

    if (createOrganization.fulfilled.match(result)) {
      onClose();
    } else {
      setError(
        result.payload?.message ||
          "Failed to create organization."
      );
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-card">
        <h2>Create Organization</h2>

        {error && (
          <div className="error-message">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Name</label>

            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label>Mission</label>

            <textarea
              name="mission"
              value={formData.mission}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label>Address</label>

            <input
              type="text"
              name="address"
              value={formData.address}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label>Contact Email</label>

            <input
              type="email"
              name="contactEmail"
              value={formData.contactEmail}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label>Contact Phone (optional)</label>

            <input
              type="tel"
              name="contactPhone"
              value={formData.contactPhone}
              onChange={handleChange}
            />
          </div>

          <div className="form-group">
            <label>Founded Year (optional)</label>

            <input
              type="number"
              name="foundedYear"
              value={formData.foundedYear}
              onChange={handleChange}
            />
          </div>

          <div className="form-group">
            <label>Coordinator ID</label>

            <input
              type="number"
              name="coordinatorId"
              value={formData.coordinatorId}
              onChange={handleChange}
              required
            />
          </div>

          <div className="modal-actions">
            <button
              type="button"
              onClick={onClose}
            >
              Cancel
            </button>

            <button
              type="submit"
              className="primary-btn"
            >
              Create Organization
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default OrganizationForm;