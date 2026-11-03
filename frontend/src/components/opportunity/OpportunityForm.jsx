import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { createOpportunity } from '../../store/slices/opportunitySlice';
import { fetchOrganizations } from '../../store/slices/organizationSlice';
import { notify } from '../../store/slices/notificationSlice';

const OpportunityForm = ({ onClose }) => {
  const dispatch = useDispatch();
  const user = useSelector((state) => state.auth?.user);
  const organizations = useSelector((state) => state.organizations?.items || []);

  useEffect(() => {
    if (user?.role === 'PLATFORM_ADMIN') {
      dispatch(fetchOrganizations({ page: 0, size: 100 }));
    }
  }, [dispatch, user?.role]);

  const [formData, setFormData] = useState({
    title: '',
    description: '',
    location: '',
    category: '',
    scheduledDate: '',
    startTime: '',
    endTime: '',
    maxCapacity: '',
    requiredSkills: '',
    organizationId: user?.organizationId || '',
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
      maxCapacity: formData.maxCapacity ? parseInt(formData.maxCapacity, 10) : 1,
      organizationId: formData.organizationId
        ? parseInt(formData.organizationId, 10)
        : user?.organizationId || null,
    };
    if (!payload.organizationId) {
      dispatch(notify('Select or assign an organization before creating an opportunity.', 'error'));
      return;
    }
    try {
      await dispatch(createOpportunity(payload)).unwrap();
      dispatch(notify('Opportunity created successfully.', 'success'));
      if (onClose) onClose();
    } catch (err) {
      dispatch(notify(err.message || 'Failed to create opportunity.', 'error'));
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-card">
        <div className="modal-header">
          <h2>Create Opportunity</h2>
          <button type="button" className="close-btn" onClick={onClose}>
            &times;
          </button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="opp-title">Title</label>
            <input
              type="text"
              id="opp-title"
              name="title"
              value={formData.title}
              onChange={handleChange}
              required
            />
          </div>

          {user?.role === 'PLATFORM_ADMIN' && (
            <div className="form-group">
              <label htmlFor="opp-organization">Organization</label>
              <select
                id="opp-organization"
                name="organizationId"
                value={formData.organizationId}
                onChange={handleChange}
                required
              >
                <option value="">Select an organization</option>
                {organizations.map((organization) => (
                  <option key={organization.id} value={organization.id}>
                    {organization.name}
                  </option>
                ))}
              </select>
            </div>
          )}

          <div className="form-group">
            <label htmlFor="opp-desc">Description</label>
            <textarea
              id="opp-desc"
              name="description"
              rows="3"
              value={formData.description}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="opp-loc">Location</label>
            <input
              type="text"
              id="opp-loc"
              name="location"
              value={formData.location}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="opp-cat">Category</label>
            <input
              type="text"
              id="opp-cat"
              name="category"
              value={formData.category}
              onChange={handleChange}
            />
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="opp-date">Scheduled Date</label>
              <input
                type="date"
                id="opp-date"
                name="scheduledDate"
                value={formData.scheduledDate}
                onChange={handleChange}
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="opp-start">Start Time</label>
              <input
                type="time"
                id="opp-start"
                name="startTime"
                value={formData.startTime}
                onChange={handleChange}
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="opp-end">End Time</label>
              <input
                type="time"
                id="opp-end"
                name="endTime"
                value={formData.endTime}
                onChange={handleChange}
                required
              />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="opp-capacity">Max Capacity</label>
            <input
              type="number"
              id="opp-capacity"
              name="maxCapacity"
              min="1"
              value={formData.maxCapacity}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="opp-skills">Required Skills</label>
            <input
              type="text"
              id="opp-skills"
              name="requiredSkills"
              value={formData.requiredSkills}
              onChange={handleChange}
            />
          </div>

          <div className="modal-actions">
            <button type="button" className="secondary-btn" onClick={onClose}>
              Cancel
            </button>
            <button type="submit" className="primary-btn">
              Create Opportunity
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default OpportunityForm;