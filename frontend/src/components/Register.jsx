import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { register } from '../store/slices/authSlice';

const PUBLIC_REGISTRATION_ROLES = ['VOLUNTEER', 'ORGANIZATION_COORDINATOR'];

const Register = () => {
  const dispatch = useDispatch();
  const { loading } = useSelector((state) => state.auth || {});

  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    username: '',
    password: '',
    phoneNumber: '',
    role: 'VOLUNTEER',
  });

  const [fieldErrors, setFieldErrors] = useState({});
  const [successMessage, setSuccessMessage] = useState('');
  const [generalError, setGeneralError] = useState('');

  const handleChange = (e) => {
    setFormData((prev) => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setFieldErrors({});
    setGeneralError('');
    setSuccessMessage('');

    const result = await dispatch(register(formData));
    if (register.fulfilled.match(result)) {
      setSuccessMessage('Registration successful');
    } else if (result.payload?.fieldErrors) {
      setFieldErrors(result.payload.fieldErrors);
    } else if (result.payload?.message) {
      setGeneralError(result.payload.message);
    }
  };

  return (
    <div className="auth-container">
      <aside className="auth-story-panel">
        <div className="auth-story-copy">
          <p className="auth-eyebrow">Community starts with you</p>
          <h2>Small acts.<br />Lasting impact.</h2>
          <blockquote>
            “We make a living by what we get, but we make a life by what we give.”
            <cite>Winston Churchill</cite>
          </blockquote>
          <div className="auth-story-stats">
            <span><strong>1,240+</strong> volunteers</span>
            <span><strong>86</strong> local causes</span>
          </div>
        </div>
      </aside>
      <div className="auth-card">
        <div className="auth-header">
          <h1>VolunteerView</h1>
          <p>Empowering communities through action</p>
        </div>

        {successMessage && (
          <div className="success-message">{successMessage}</div>
        )}
        {generalError && (
          <div className="error-message">{generalError}</div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="fullName">Full Name</label>
            <input
              type="text"
              id="fullName"
              name="fullName"
              placeholder="John Doe"
              value={formData.fullName}
              onChange={handleChange}
              required
            />
            {fieldErrors.fullName && (
              <span className="error-message">{fieldErrors.fullName}</span>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              type="email"
              id="email"
              name="email"
              placeholder="john@example.com"
              value={formData.email}
              onChange={handleChange}
              required
            />
            {fieldErrors.email && (
              <span className="error-message">{fieldErrors.email}</span>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="username">Username</label>
            <input
              type="text"
              id="username"
              name="username"
              placeholder="johndoe"
              value={formData.username}
              onChange={handleChange}
              required
            />
            {fieldErrors.username && (
              <span className="error-message">{fieldErrors.username}</span>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              type="password"
              id="password"
              name="password"
              placeholder="••••••••"
              value={formData.password}
              onChange={handleChange}
              required
            />
            {fieldErrors.password && (
              <span className="error-message">{fieldErrors.password}</span>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="phoneNumber">Phone Number (Optional)</label>
            <input
              type="text"
              id="phoneNumber"
              name="phoneNumber"
              value={formData.phoneNumber}
              onChange={handleChange}
            />
          </div>

          <div className="form-group">
            <label htmlFor="role">Role</label>
            <select
              id="role"
              name="role"
              value={formData.role}
              onChange={handleChange}
            >
              {PUBLIC_REGISTRATION_ROLES.map((role) => (
                <option key={role} value={role}>{role}</option>
              ))}
            </select>
          </div>

          <button
            type="submit"
            className="primary-btn full-width"
            disabled={loading}
          >
            {loading ? 'Registering...' : 'Register'}
          </button>
        </form>

        <div className="auth-footer">
          <p>
            Already have an account? <Link to="/login">Sign In</Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Register;