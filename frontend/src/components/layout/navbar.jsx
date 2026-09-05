import React from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { logout } from '../../store/slices/authSlice';

const Navbar = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const user = useSelector((state) => state.auth?.user);

  const handleLogout = () => {
    dispatch(logout());
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="nav-container">
        <Link to="/" className="brand">
          VolunteerView
        </Link>
        <div className="nav-links">
          <NavLink to="/" end>
            Home
          </NavLink>
          <NavLink to="/organizations">Organizations</NavLink>
          <NavLink to="/opportunities">Opportunities</NavLink>
          <NavLink to="/enrollments">Enrollments</NavLink>
          <NavLink to="/impact-reports">Impact Reports</NavLink>
          {user?.role === 'PLATFORM_ADMIN' && (
            <NavLink to="/users">Users</NavLink>
          )}
        </div>
        {user && (
          <div className="nav-user">
            <span>Welcome back! {user.fullName}</span>
            <button type="button" className="logout-btn" onClick={handleLogout}>
              Logout
            </button>
          </div>
        )}
      </div>
    </nav>
  );
};

export default Navbar;