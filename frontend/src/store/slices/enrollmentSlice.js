import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import enrollmentService from '../../services/enrollmentService';

export const fetchEnrollments = createAsyncThunk(
  'enrollments/fetchEnrollments',
  async ({ page = 0, size = 10 } = {}, { getState, rejectWithValue }) => {
    try {
      const { auth } = getState();
      const user = auth.user;

      if (!user) {
        return rejectWithValue({ message: 'User not authenticated' });
      }

      if (user.role === 'VOLUNTEER') {
        const data = await enrollmentService.getMyEnrollments(page, size);
        return data;
      } else if (user.role === 'ORGANIZATION_COORDINATOR') {
        if (!user.organizationId) {
          return rejectWithValue({ message: 'You are not assigned to any organization.' });
        }
        const data = await enrollmentService.getEnrollmentsByOrg(user.organizationId, page, size);
        return data;
      } else {
        const data = await enrollmentService.getAll(page, size);
        return data;
      }
    } catch (err) {
      return rejectWithValue(err.response?.data || { message: err.message });
    }
  }
);

export const enrollInOpportunity = createAsyncThunk(
  'enrollments/enrollInOpportunity',
  async (enrollData, { rejectWithValue }) => {
    try {
      const data = await enrollmentService.enroll(enrollData);
      return data;
    } catch (err) {
      return rejectWithValue(err.response?.data || { message: err.message });
    }
  }
);

export const approveEnrollment = createAsyncThunk(
  'enrollments/approveEnrollment',
  async (id, { rejectWithValue }) => {
    try {
      const data = await enrollmentService.approve(id);
      return data;
    } catch (err) {
      return rejectWithValue(err.response?.data || { message: err.message });
    }
  }
);

export const rejectEnrollment = createAsyncThunk(
  'enrollments/rejectEnrollment',
  async (id, { rejectWithValue }) => {
    try {
      const data = await enrollmentService.reject(id);
      return data;
    } catch (err) {
      return rejectWithValue(err.response?.data || { message: err.message });
    }
  }
);

export const checkInEnrollment = createAsyncThunk(
  'enrollments/checkInEnrollment',
  async (id, { rejectWithValue }) => {
    try {
      const data = await enrollmentService.checkIn(id);
      return data;
    } catch (err) {
      return rejectWithValue(err.response?.data || { message: err.message });
    }
  }
);

export const completeEnrollment = createAsyncThunk(
  'enrollments/completeEnrollment',
  async ({ id, hours }, { rejectWithValue }) => {
    try {
      const data = await enrollmentService.complete(id, hours);
      return data;
    } catch (err) {
      return rejectWithValue(err.response?.data || { message: err.message });
    }
  }
);

const initialState = {
  enrollments: [],
  items: [],
  loading: false,
  error: null,
  totalPages: 0,
  filterStatus: 'ALL',
};

const enrollmentSlice = createSlice({
  name: 'enrollments',
  initialState,
  reducers: {
    setFilterStatus: (state, action) => {
      state.filterStatus = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder
      // fetchEnrollments
      .addCase(fetchEnrollments.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchEnrollments.fulfilled, (state, action) => {
        state.loading = false;
        const list = action.payload.content || action.payload || [];
        state.enrollments = list;
        state.items = list;
        state.totalPages = action.payload.totalPages || 0;
      })
      .addCase(fetchEnrollments.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // enrollInOpportunity
      .addCase(enrollInOpportunity.fulfilled, (state, action) => {
        state.enrollments.unshift(action.payload);
        state.items.unshift(action.payload);
      })
      // Status update actions
      .addMatcher(
        (action) =>
          [
            approveEnrollment.fulfilled.type,
            rejectEnrollment.fulfilled.type,
            checkInEnrollment.fulfilled.type,
            completeEnrollment.fulfilled.type,
          ].includes(action.type),
        (state, action) => {
          const index = state.enrollments.findIndex((e) => e.id === action.payload.id);
          if (index !== -1) {
            state.enrollments[index] = action.payload;
            state.items[index] = action.payload;
          }
        }
      );
  },
});

export const { setFilterStatus } = enrollmentSlice.actions;
export default enrollmentSlice.reducer;