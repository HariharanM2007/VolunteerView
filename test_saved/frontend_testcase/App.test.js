import React, { useState, useEffect } from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';
import { MemoryRouter, BrowserRouter } from 'react-router-dom';
import axios from 'axios';

// Mock axios
jest.mock('../services/api', () => ({
  __esModule: true,
  default: {
    get: jest.fn(),
    post: jest.fn(),
    put: jest.fn(),
    delete: jest.fn(),
    interceptors: {
      request: { use: jest.fn() },
      response: { use: jest.fn() }
    }
  }
}));

// Mock react-redux useDispatch
jest.mock('react-redux', () => {
  const actual = jest.requireActual('react-redux');
  return {
    ...actual,
    useDispatch: jest.fn()
  };
});

// Setup default behavior for useDispatch
beforeEach(() => {
  const mockDispatch = jest.fn((action) => {
    if (typeof action === 'function') {
      return action(mockDispatch, () => ({}));
    }
    return action;
  });
  require('react-redux').useDispatch.mockReturnValue(mockDispatch);
});

// Mock store setup
const authReducer = (state = { user: { fullName: 'John Doe', role: 'VOLUNTEER' }, isAuthenticated: true }, action) => {
  switch (action.type) {
    case 'auth/loginSuccess': return { ...state, user: action.payload, isAuthenticated: true };
    case 'auth/logout': return { ...state, user: null, isAuthenticated: false };
    default: return state;
  }
};
const store = configureStore({
  reducer: {
    auth: authReducer,
    enrollments: () => ({ enrollments: [] }),
    opportunities: () => ({ items: [] })
  }
});

describe('Frontend Tests (VolunteerView)', () => {

  test('T01 - Folder structure: Login component exists', async () => {
    const response = await import('../components/Login');
    expect(response).toBeDefined();
    expect(response.default).toBeDefined();

    const { default: Login } = response;
    render(<Provider store={store}><BrowserRouter><Login /></BrowserRouter></Provider>);
    expect(screen.getByRole('button', { name: /Sign In/i })).toBeInTheDocument();
  });

  test('T02 - Folder structure: services directory exists', async () => {
    const response = await import('../services/opportunityService');
    expect(response).toBeDefined();
    expect(response.default.getAll).toBeDefined();
  });

  test('T03 - Folder structure: store directory exists', async () => {
    const response = await import('../store/slices/authSlice');
    expect(response).toBeDefined();
    expect(typeof response.default).toBe('function');
  });

  test('T04 - Folder structure: styling check', async () => {
    const App = (await import('../App')).default;
    const store = configureStore({
      reducer: {
        auth: () => ({ user: { role: 'VOLUNTEER' } }),
        enrollments: () => ({ enrollments: [] }),
        organizations: () => ({ items: [] }),
        opportunities: () => ({ items: [] }),
        impactReports: () => ({ items: [] }),
        notifications: () => ({ list: [] })
      }
    });
    const { container } = render(<Provider store={store}><App /></Provider>);
    expect(container.querySelector('.app-container')).toBeInTheDocument();
  });

  test('T05 - Folder structure: layout directory exists', async () => {
    const response = await import('../components/layout/Navbar');
    expect(response).toBeDefined();
    expect(response.default).toBeDefined();

    const { default: Navbar } = response;
    render(<Provider store={store}><BrowserRouter ><Navbar /></BrowserRouter ></Provider>);
    expect(screen.getByRole('navigation')).toBeInTheDocument();
  });

  test('T06 - Navbar: Displays navigation links', async () => {
    const { default: Navbar } = await import('../components/layout/Navbar');
    render(<Provider store={store}><BrowserRouter><Navbar /></BrowserRouter></Provider>);
    expect(screen.getByRole('navigation')).toBeInTheDocument();
  });

  test('T07 - OpportunityList: Fetches and displays opportunities from the backend', async () => {
    const mockData = [{ id: 1, title: 'Community Clean Up', description: 'Help clean the local park.', location: 'Park', status: 'OPEN', enrolledCount: 0, maxCapacity: 10 }];

    const mockStore = configureStore({
      reducer: {
        auth: () => ({ user: { role: 'VOLUNTEER' } }),
        enrollments: () => ({ enrollments: [] }),
        opportunities: () => ({ items: mockData, loading: false, searchQuery: '' })
      }
    });

    const OpportunityList = (await import('../components/opportunity/OpportunityList')).default;
    render(<Provider store={mockStore}><OpportunityList /></Provider>);

    expect(screen.getByText('Community Clean Up')).toBeInTheDocument();
  });

  test('T08 - useFetch: Hook logic (Verifies API call)', async () => {
    const api = (await import('../services/api')).default;
    api.get.mockResolvedValue({ data: [] });
    const OpportunityList = (await import('../components/opportunity/OpportunityList')).default;
    render(<Provider store={store}><OpportunityList /></Provider>);
    expect(api.get).toHaveBeenCalled();
  });

  test('T09 - OpportunityForm: Submits data to backend', async () => {
    const mockDispatch = jest.fn();
    require('react-redux').useDispatch.mockReturnValue(mockDispatch);

    const OpportunityForm = (await import('../components/opportunity/OpportunityForm')).default;
    const mockStore = configureStore({
      reducer: {
        auth: () => ({ user: { role: 'VOLUNTEER' } }),
        enrollments: () => ({ enrollments: [] }),
        organizations: () => ({ items: [] }),
        opportunities: () => ({ items: [] })
      }
    });
    const { container } = render(<Provider store={mockStore}><OpportunityForm onClose={() => { }} /></Provider>);

    const form = container.querySelector('form');
    expect(form).toBeInTheDocument();
    fireEvent.submit(form);

    expect(mockDispatch).toHaveBeenCalled();
  });

  test('T10 - Redux: Verifies authSlice updates global state', async () => {
    const { default: authReducer } = await import('../store/slices/authSlice');
    const state = authReducer({ user: null }, { type: 'auth/login/fulfilled', payload: { role: 'VOLUNTEER' } });
    expect(state.user.role).toBe('VOLUNTEER');
  });

  test('T11 - Routing: Protected content logic', async () => {
    const Home = (await import('../components/dashboard/Home')).default;
    render(<Provider store={store}><Home /></Provider>);
    expect(screen.getByText(/Welcome/i)).toBeInTheDocument();
  });

  test('T12 - Login: Input fields should have correct placeholders', async () => {
    const { default: Login } = await import('../components/Login');
    render(<Provider store={store}><BrowserRouter><Login /></BrowserRouter></Provider>);

    const usernameField = screen.getByPlaceholderText(/Your username/i);
    const passField = screen.getByPlaceholderText('••••••••');

    expect(usernameField).toBeInTheDocument();
    expect(passField).toBeInTheDocument();
  });

  test('T13 - OpportunityList: Filters opportunities by search query', async () => {
    const mockData = [
      { id: 1, title: 'Clean Up', organizationName: 'Org A', status: 'OPEN', enrolledCount: 0, maxCapacity: 10 },
      { id: 2, title: 'Soup Kitchen', organizationName: 'Org B', status: 'OPEN', enrolledCount: 0, maxCapacity: 10 }
    ];

    const mockStore = configureStore({
      reducer: {
        auth: () => ({ user: { role: 'VOLUNTEER' } }),
        enrollments: () => ({ enrollments: [] }),
        opportunities: () => ({ items: mockData, loading: false, searchQuery: 'Soup' })
      }
    });

    const OpportunityList = (await import('../components/opportunity/OpportunityList')).default;
    render(<Provider store={mockStore}><OpportunityList /></Provider>);

    expect(screen.queryByText('Clean Up')).not.toBeInTheDocument();
    expect(screen.getByText('Soup Kitchen')).toBeInTheDocument();
  });

  test('T14 - OpportunityForm: Validates input types', async () => {
    const OpportunityForm = (await import('../components/opportunity/OpportunityForm')).default;
    const mockStore = configureStore({
      reducer: {
        auth: () => ({ user: { role: 'VOLUNTEER' } }),
        enrollments: () => ({ enrollments: [] }),
        organizations: () => ({ items: [] }),
        opportunities: () => ({ items: [] })
      }
    });
    render(<Provider store={mockStore}><OpportunityForm onClose={() => { }} /></Provider>);
    expect(screen.getByRole('heading', { name: /Create Opportunity/i })).toBeInTheDocument();
  });

  test('T15 - OpportunityList: Identity validation', async () => {
    const OpportunityList = (await import('../components/opportunity/OpportunityList')).default;
    render(<Provider store={store}><OpportunityList /></Provider>);
    expect(screen.getByRole('heading', { name: /Volunteer Opportunities/i })).toBeInTheDocument();
  });

  test('T16 - ImpactReportForm: Renders heading and form elements', async () => {
    const ImpactReportForm = (await import('../components/impact-report/ImpactReportForm')).default;
    const mockStore = configureStore({
      reducer: {
        enrollments: () => ({ enrollments: [] }),
        auth: () => ({ user: { role: 'VOLUNTEER' } })
      }
    });
    render(<Provider store={mockStore}><ImpactReportForm onClose={() => { }} /></Provider>);
    expect(screen.getByText(/Submit Impact Report/i)).toBeInTheDocument();
  });

  test('T17 - Style: Buttons follow professional constraints', async () => {
    const Home = (await import('../components/dashboard/Home')).default;
    const mockStore = configureStore({ reducer: { auth: () => ({ user: { role: 'VOLUNTEER' } }), enrollments: () => ({ items: [] }) } });
    render(<Provider store={mockStore}><Home /></Provider>);
    const btn = screen.getByRole('button', { name: /Explore Opportunities/i });
    expect(btn).toHaveClass('primary-btn');
  });

  test('T18 - Style: Containers maintain consistent layout', async () => {
    const Home = (await import('../components/dashboard/Home')).default;
    const mockStore = configureStore({ reducer: { auth: () => ({ user: { role: 'VOLUNTEER' } }), enrollments: () => ({ items: [] }) } });
    const { container } = render(<Provider store={mockStore}><Home /></Provider>);
    expect(container.querySelector('.dashboard')).toBeInTheDocument();
  });

  test('T19 - Layout: Navbar renders navigation links', async () => {
    const { default: Navbar } = await import('../components/layout/Navbar');
    render(<Provider store={store}><BrowserRouter><Navbar /></BrowserRouter></Provider>);
    expect(screen.getByRole('navigation')).toBeInTheDocument();
  });

  test('T20 - Home: Dashboard hero section welcome message', async () => {
    const Home = (await import('../components/dashboard/Home')).default;
    const mockStore = configureStore({ reducer: { auth: () => ({ user: { fullName: 'John Doe', role: 'VOLUNTEER' } }), enrollments: () => ({ items: [] }) } });
    render(<Provider store={mockStore}><Home /></Provider>);
    expect(screen.getByText(/Welcome, John Doe!/i)).toBeInTheDocument();
  });

  test('T21 - EnrollmentList: Renders list of enrollments', async () => {
    const EnrollmentList = (await import('../components/enrollment/EnrollmentList')).default;
    const mockStore = configureStore({
      reducer: {
        auth: () => ({ user: { role: 'VOLUNTEER' } }),
        enrollments: () => ({ enrollments: [{ id: 1, opportunityTitle: 'Help Kids', status: 'ENROLLED' }], loading: false, filterStatus: 'ALL' })
      }
    });
    render(<Provider store={mockStore}><BrowserRouter><EnrollmentList /></BrowserRouter></Provider>);
    expect(screen.getByText('Help Kids')).toBeInTheDocument();
  });

  test('T22 - EnrollmentList: Displays empty state when no enrollments', async () => {
    const EnrollmentList = (await import('../components/enrollment/EnrollmentList')).default;
    const mockStore = configureStore({
      reducer: {
        auth: () => ({ user: { role: 'VOLUNTEER' } }),
        enrollments: () => ({ enrollments: [], loading: false })
      }
    });
    render(<Provider store={mockStore}><BrowserRouter><EnrollmentList /></BrowserRouter></Provider>);
    expect(screen.getByText(/No enrollments found/i)).toBeInTheDocument();
  });

  test('T23 - ImpactReportList: Renders list of impact reports', async () => {
    const ImpactReportList = (await import('../components/impact-report/ImpactReportList')).default;
    const mockStore = configureStore({
      reducer: {
        auth: () => ({ user: { role: 'VOLUNTEER' } }),
        impactReports: () => ({ items: [{ id: 1, summary: 'Good year', volunteerName: 'John Doe', organizationName: 'Org A', opportunityTitle: 'Help', hoursContributed: 10, beneficiariesServed: 5, rating: 5 }], loading: false })
      }
    });
    render(<Provider store={mockStore}><ImpactReportList /></Provider>);
    expect(screen.getByText('John Doe')).toBeInTheDocument();
  });

  test('T24 - ImpactReportList: Displays empty state when no reports', async () => {
    const ImpactReportList = (await import('../components/impact-report/ImpactReportList')).default;
    const mockStore = configureStore({
      reducer: {
        auth: () => ({ user: { role: 'VOLUNTEER' } }),
        impactReports: () => ({ items: [], loading: false })
      }
    });
    render(<Provider store={mockStore}><ImpactReportList /></Provider>);
    expect(screen.getByText(/No Impact Reports Yet/i)).toBeInTheDocument();
  });

  test('T25 - Register: Renders registration form fields', async () => {
    const Register = (await import('../components/Register')).default;
    render(<Provider store={store}><BrowserRouter><Register /></BrowserRouter></Provider>);
    expect(screen.getByPlaceholderText('John Doe')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('john@example.com')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('johndoe')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('••••••••')).toBeInTheDocument();
  });

  test('T26 - Register: Successful submission shows success message', async () => {
    const Register = (await import('../components/Register')).default;
    const api = (await import('../services/api')).default;
    api.post.mockResolvedValue({ data: { message: 'Success' } });

    render(<Provider store={store}><BrowserRouter><Register /></BrowserRouter></Provider>);

    fireEvent.change(screen.getByPlaceholderText('John Doe'), { target: { value: 'John Doe' } });
    fireEvent.change(screen.getByPlaceholderText('john@example.com'), { target: { value: 'john@example.com' } });
    fireEvent.change(screen.getByPlaceholderText('johndoe'), { target: { value: 'johndoe' } });
    fireEvent.change(screen.getByPlaceholderText('••••••••'), { target: { value: 'password' } });

    fireEvent.click(screen.getByRole('button', { name: /Register/i }));

    await waitFor(() => {
      expect(screen.getByText(/Registration successful/i)).toBeInTheDocument();
    });
  });

  test('T27 - Login: Shows error message on failed login', async () => {
    const { default: Login } = await import('../components/Login');
    const mockStore = configureStore({
      reducer: {
        auth: () => ({ error: 'Invalid credentials', loading: false })
      }
    });
    render(<Provider store={mockStore}><BrowserRouter><Login /></BrowserRouter></Provider>);
    expect(screen.getByText(/Invalid username or password/i)).toBeInTheDocument();
  });

  test('T28 - Register: Shows field errors from backend', async () => {
    const Register = (await import('../components/Register')).default;
    const api = (await import('../services/api')).default;
    api.post.mockRejectedValue({
      response: {
        data: {
          fieldErrors: {
            username: 'Username already taken'
          }
        }
      }
    });

    render(<Provider store={store}><BrowserRouter><Register /></BrowserRouter></Provider>);

    fireEvent.change(screen.getByPlaceholderText('johndoe'), { target: { value: 'taken' } });
    fireEvent.click(screen.getByRole('button', { name: /Register/i }));

    await waitFor(() => {
      expect(screen.getByText('Username already taken')).toBeInTheDocument();
    });
  });

  test('T29 - Home: Renders quick actions for VOLUNTEER', async () => {
    const Home = (await import('../components/dashboard/Home')).default;
    const mockStore = configureStore({
      reducer: {
        auth: () => ({ user: { role: 'VOLUNTEER', fullName: 'John' } }),
        enrollments: () => ({ items: [] })
      }
    });
    render(<Provider store={mockStore}><Home /></Provider>);
    expect(screen.getByText(/Explore Opportunities/i)).toBeInTheDocument();
  });

  test('T30 - Home: Renders admin stats for PLATFORM_ADMIN', async () => {
    const Home = (await import('../components/dashboard/Home')).default;
    const mockStore = configureStore({
      reducer: {
        auth: () => ({ user: { role: 'PLATFORM_ADMIN', fullName: 'Admin' } }),
        enrollments: () => ({ items: [] })
      }
    });
    const api = (await import('../services/api')).default;
    api.get.mockResolvedValue({ data: { totalVolunteers: 10, totalOpportunities: 5 } });

    render(<Provider store={mockStore}><Home /></Provider>);
    expect(screen.queryByText(/Explore Opportunities/i)).not.toBeInTheDocument();
  });

});
