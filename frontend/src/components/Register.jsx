import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";

import { register } from "../store/slices/authSlice";

const Register = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const { loading, error } = useSelector((state) => state.auth);

  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    username: "",
    password: "",
    phoneNumber: "",
    role: "VOLUNTEER",
  });

  const [success, setSuccess] = useState(false);
  const [fieldErrors, setFieldErrors] = useState({});

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));

    setFieldErrors((prev) => ({
      ...prev,
      [name]: "",
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    setFieldErrors({});
    setSuccess(false);

    const result = await dispatch(register(formData));

    if (register.fulfilled.match(result)) {
      setSuccess(true);

      setTimeout(() => {
        navigate("/");
      }, 1000);

      return;
    }

    if (result.payload?.fieldErrors) {
      setFieldErrors(result.payload.fieldErrors);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <div className="auth-header">
          <div className="logo">🤝</div>

          <h1>VolunteerView</h1>

          <p>Empowering communities through action</p>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="fullName">Full Name</label>

            <input
              id="fullName"
              type="text"
              name="fullName"
              
              value={formData.fullName}
              onChange={handleChange}
              required
            />

            {fieldErrors.fullName && (
              <div className="field-error">
                {fieldErrors.fullName}
              </div>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="email">Email</label>

            <input
              id="email"
              type="email"
              name="email"
              
              value={formData.email}
              onChange={handleChange}
              required
            />

            {fieldErrors.email && (
              <div className="field-error">
                {fieldErrors.email}
              </div>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="username">Username</label>

            <input
              id="username"
              type="text"
              name="username"
              
              value={formData.username}
              onChange={handleChange}
              required
            />

            {fieldErrors.username && (
              <div className="field-error">
                {fieldErrors.username}
              </div>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>

            <input
              id="password"
              type="password"
              name="password"
              
              value={formData.password}
              onChange={handleChange}
              required
            />

            {fieldErrors.password && (
              <div className="field-error">
                {fieldErrors.password}
              </div>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="phoneNumber">
              Phone Number <span>(optional)</span>
            </label>

            <input
              id="phoneNumber"
              type="tel"
              name="phoneNumber"
             
              value={formData.phoneNumber}
              onChange={handleChange}
            />

            {fieldErrors.phoneNumber && (
              <div className="field-error">
                {fieldErrors.phoneNumber}
              </div>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="role">Role</label>

            <select
              id="role"
              name="role"
              value={formData.role}
              onChange={handleChange}
              required
            >
              <option value="VOLUNTEER">Volunteer</option>
              <option value="ORGANIZATION_COORDINATOR">
                Organization Coordinator
              </option>
              <option value="PLATFORM_ADMIN">
                Platform Admin
              </option>
            </select>

            {fieldErrors.role && (
              <div className="field-error">
                {fieldErrors.role}
              </div>
            )}
          </div>

          {error?.message && !Object.keys(fieldErrors).length && (
            <div className="error-message">
              {error.message}
            </div>
          )}

          {success && (
            <div className="success-message">
              Registration successful
            </div>
          )}

          <button
            type="submit"
            className="primary-btn full-width"
            disabled={loading}
          >
            {loading ? "Registering..." : "Register"}
          </button>
        </form>

        <div className="auth-footer">
          <p>
            Already have an account?{" "}
            <Link to="/login">Sign in</Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Register;