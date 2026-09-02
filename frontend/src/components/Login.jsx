import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";

import { login, clearError } from "../store/slices/authSlice";

const Login = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const { user, loading, error } = useSelector(
    (state) => state.auth
  );

  const [credentials, setCredentials] = useState({
    username: "",
    password: "",
  });

  const [showPassword, setShowPassword] = useState(false);
  const [usernameError, setUsernameError] = useState("");

  useEffect(() => {
    if (user) {
      navigate("/");
    }
  }, [user, navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;

    if (name === "username") {
      if (!/^[A-Za-z]*$/.test(value)) {
        setUsernameError(
          "Username must contain only letters (A-Z)."
        );
        return;
      }

      setUsernameError("");
    }

    setCredentials((prev) => ({
      ...prev,
      [name]: value,
    }));

    if (error) {
      dispatch(clearError());
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!/^[A-Za-z]+$/.test(credentials.username)) {
      setUsernameError(
        "Username must contain only letters (A-Z)."
      );
      return;
    }

    const result = await dispatch(login(credentials));

    if (login.fulfilled.match(result)) {
      navigate("/");
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">

        {/* Header */}
        <div className="auth-header">
          <div className="logo"></div>

          <h1>VolunteerView</h1>

          <p>Empowering communities through action</p>
        </div>

        <form onSubmit={handleSubmit}>

          {/* Username */}
          <div className="form-group">
            <label htmlFor="username">
              Username
            </label>

            <input
              id="username"
              type="text"
              name="username"
              placeholder="Your username"
              value={credentials.username}
              onChange={handleChange}
              required
            />

            <small>
              Username must contain letters only.
            </small>

            {usernameError && (
              <div className="field-error">
                {usernameError}
              </div>
            )}
          </div>

          {/* Password */}
          <div className="form-group">
            <label htmlFor="password">
              Password
            </label>

            <div className="password-wrapper">
              <input
                id="password"
                type={
                  showPassword
                    ? "text"
                    : "password"
                }
                name="password"
                placeholder="••••••••"
                value={credentials.password}
                onChange={handleChange}
                required
              />

              <button
                type="button"
                className="password-toggle"
                onClick={() =>
                  setShowPassword((prev) => !prev)
                }
              >
                {showPassword ? "Hide" : "Show"}
              </button>
            </div>
          </div>

          {/* Backend error */}
          {error && (
            <div className="error-message">
              {error.message ||
                "Invalid username or password. Please try again."}
            </div>
          )}

          {/* Submit */}
          <button
            type="submit"
            className="primary-btn full-width"
            disabled={loading}
          >
            {loading
              ? "Authenticating..."
              : "Sign In"}
          </button>
        </form>

        {/* Footer */}
        <div className="auth-footer">
          <p>
            Don't have an account?{" "}
            <Link to="/register">
              Create one
            </Link>
          </p>
        </div>

      </div>
    </div>
  );
};

export default Login;