import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  fetchImpactReports,
  submitImpactReport,
} from '../../store/slices/impactReportSlice';
import { notify } from '../../store/slices/notificationSlice';

const ImpactReportForm = ({ onClose }) => {
  const dispatch = useDispatch();
  const enrollments = useSelector(
    (state) => state.enrollments?.enrollments || state.enrollments?.items || []
  );

  const completedEnrollments = enrollments.filter(
    (e) => e.status === 'COMPLETED'
  );

  const [formData, setFormData] = useState({
    enrollmentId: completedEnrollments[0]?.id || '',
    summary: '',
    hoursContributed: '',
    beneficiariesServed: '',
    rating: 5,
  });

  useEffect(() => {
    if (!formData.enrollmentId && completedEnrollments.length > 0) {
      setFormData((prev) => ({
        ...prev,
        enrollmentId: completedEnrollments[0].id,
      }));
    }
  }, [completedEnrollments, formData.enrollmentId]);

  const handleChange = (e) => {
    setFormData((prev) => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const payload = {
      enrollmentId: parseInt(formData.enrollmentId, 10),
      summary: formData.summary,
      hoursContributed: parseFloat(formData.hoursContributed),
      beneficiariesServed: parseInt(formData.beneficiariesServed, 10),
      rating: parseInt(formData.rating, 10),
    };
    try {
      await dispatch(submitImpactReport(payload)).unwrap();
      await dispatch(fetchImpactReports({ page: 0, size: 10 })).unwrap();
      dispatch(notify('Impact report submitted successfully.', 'success'));
      if (onClose) onClose();
    } catch (err) {
      dispatch(notify(err.message || 'Failed to submit impact report.', 'error'));
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-card">
        <div className="modal-header">
          <h2>Submit Impact Report</h2>
          <button type="button" className="close-btn" onClick={onClose}>
            &times;
          </button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="rep-enrollment">Select Completed Opportunity</label>
            <select
              id="rep-enrollment"
              name="enrollmentId"
              value={formData.enrollmentId}
              onChange={handleChange}
              required
            >
              <option value="">-- Choose Opportunity --</option>
              {completedEnrollments.map((e) => (
                <option key={e.id} value={e.id}>
                  {e.opportunityTitle || `Opportunity #${e.opportunityId}`}
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="rep-summary">Summary</label>
            <textarea
              id="rep-summary"
              name="summary"
              rows="4"
              placeholder="Describe your volunteering experience and community impact..."
              value={formData.summary}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="rep-hours">Hours Contributed</label>
              <input
                type="number"
                step="0.5"
                min="0.5"
                id="rep-hours"
                name="hoursContributed"
                value={formData.hoursContributed}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="rep-beneficiaries">Beneficiaries Served</label>
              <input
                type="number"
                min="0"
                id="rep-beneficiaries"
                name="beneficiariesServed"
                value={formData.beneficiariesServed}
                onChange={handleChange}
                required
              />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="rep-rating">Rating (1 to 5)</label>
            <select
              id="rep-rating"
              name="rating"
              value={formData.rating}
              onChange={handleChange}
            >
              <option value="5">5 - Excellent</option>
              <option value="4">4 - Very Good</option>
              <option value="3">3 - Good</option>
              <option value="2">2 - Fair</option>
              <option value="1">1 - Poor</option>
            </select>
          </div>

          <div className="modal-actions">
            <button type="button" className="secondary-btn" onClick={onClose}>
              Cancel
            </button>
            <button type="submit" className="primary-btn">
              Submit Report
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ImpactReportForm;