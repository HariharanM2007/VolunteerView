import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import impactReportService from '../../services/impactReportService';

export const fetchImpactReports = createAsyncThunk(
  'impactReports/fetchImpactReports',
  async ({ page = 0, size = 10 } = {}, { getState, rejectWithValue }) => {
    try {
      const { auth } = getState();
      const user = auth.user;

      if (!user) {
        return rejectWithValue({ message: 'User not authenticated' });
      }

      if (user.role === 'VOLUNTEER') {
        const data = await impactReportService.getMyReports(page, size);
        return data;
      } else if (user.role === 'ORGANIZATION_COORDINATOR') {
        if (!user.organizationId) {
          return rejectWithValue({ message: 'You are not assigned to any organization.' });
        }
        const data = await impactReportService.getReportsByOrg(user.organizationId);
        return data;
      } else {
        const data = await impactReportService.getAll(page, size);
        return data;
      }
    } catch (err) {
      return rejectWithValue(err.response?.data || { message: err.message });
    }
  }
);

export const submitImpactReport = createAsyncThunk(
  'impactReports/submitImpactReport',
  async (reportData, { rejectWithValue }) => {
    try {
      const data = await impactReportService.submit(reportData);
      return data;
    } catch (err) {
      return rejectWithValue(err.response?.data || { message: err.message });
    }
  }
);

const initialState = {
  items: [],
  loading: false,
  error: null,
  totalPages: 0,
};

const impactReportSlice = createSlice({
  name: 'impactReports',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      // fetchImpactReports
      .addCase(fetchImpactReports.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchImpactReports.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload.content || action.payload || [];
        state.totalPages = action.payload.totalPages || 0;
      })
      .addCase(fetchImpactReports.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // submitImpactReport
      .addCase(submitImpactReport.fulfilled, (state, action) => {
        state.items.unshift(action.payload);
        state.error = null;
      })
      .addCase(submitImpactReport.rejected, (state, action) => {
        state.error = action.payload;
      });
  },
});

export default impactReportSlice.reducer;