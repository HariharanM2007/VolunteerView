import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { createOrganization } from '../../store/slices/organizationSlice';

const OrganizationForm = ({ onClose }) => {
  const dispatch = useDispatch();
  const user = useSelector((state) => state.auth?.user);

  const [formData, setFormData] = useState({
    name: '',
    mission: '',
    address: '',
    contactEmail: '',
    contactPhone: '',
    foundedYear: '',
    coordinatorId: user?.id || '',
  });

  const handleChange = (e) => {
    setFormData((prev) => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const payload = {
      ...formData,
      foundedYear: formData.foundedYear ? parseInt(formData.foundedYear, 10) : null,
      coordinatorId: formData.coordinatorId ? parseInt(formData.coordinatorId, 10) : user?.id,
    };
    await dispatch(createOrganization(payload));
    if (onClose) onClose();
  };

  return (
    <div className="modal-overlay">
      <div className="modal-card">
        <div className="modal-header">
          <h2>Add Organization</h2>
          <button type="button" className="close-btn" onClick={onClose}>
            &times;
          </button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="org-name">Organization Name</label>
            <input
              type="text"
              id="org-name"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="org-mission">Mission</label>
            <textarea
              id="org-mission"
              name="mission"
              rows="3"
              value={formData.mission}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="org-address">Address</label>
            <input
              type="text"
              id="org-address"
              name="address"
              value={formData.address}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="org-email">Contact Email</label>
            <input
              type="email"
              id="org-email"
              name="contactEmail"
              value={formData.contactEmail}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="org-phone">Contact Phone (Optional)</label>
            <input
              type="text"
              id="org-phone"
              name="contactPhone"
              value={formData.contactPhone}
              onChange={handleChange}
            />
          </div>

          <div className="form-group">
            <label htmlFor="org-year">Founded Year (Optional)</label>
            <input
              type="number"
              id="org-year"
              name="foundedYear"
              min="1800"
              max="2100"
              value={formData.foundedYear}
              onChange={handleChange}
            />
          </div>

          <div className="modal-actions">
            <button type="button" className="secondary-btn" onClick={onClose}>
              Cancel
            </button>
            <button type="submit" className="primary-btn">
              Create Organization
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default OrganizationForm;