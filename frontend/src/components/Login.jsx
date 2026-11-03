import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { login } from '../store/slices/authSlice';

const Login = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { loading, error } = useSelector((state) => state.auth || {});

  const [credentials, setCredentials] = useState({
    username: '',
    password: '',
  });

  const handleChange = (e) => {
    setCredentials((prev) => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const result = await dispatch(login(credentials));
    if (login.fulfilled.match(result)) {
      navigate('/');
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

        {error && (
          <div className="error-message">
            {error.message || 'Invalid username or password. Please try again.'}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="username">Username</label>
            <input
              type="text"
              id="username"
              name="username"
              placeholder="Your username"
              value={credentials.username}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              type="password"
              id="password"
              name="password"
              placeholder="••••••••"
              value={credentials.password}
              onChange={handleChange}
              required
            />
          </div>

          <button
            type="submit"
            className="primary-btn full-width"
            disabled={loading}
          >
            {loading ? 'Authenticating...' : 'Sign In'}
          </button>
        </form>

        <div className="auth-footer">
          <p>
            Don't have an account? <Link to="/register">Create one</Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;