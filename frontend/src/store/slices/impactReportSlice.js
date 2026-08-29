import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import impactReportService from "../../services/impactReportService";

const initialState = {
  items: [],
  loading: false,
  error: null,
  totalPages: 0,
};

export const fetchImpactReports = createAsyncThunk(
  "impactReports/fetchImpactReports",
  async (_, thunkAPI) => {
    try {
      const user = thunkAPI.getState().auth.user;

      if (user?.role === "VOLUNTEER") {
        return await impactReportService.getMyReports();
      }

      if (user?.role === "ORGANIZATION_COORDINATOR") {
        if (!user.organizationId) {
          return thunkAPI.rejectWithValue({
            message: "You are not assigned to any organization.",
          });
        }

        return await impactReportService.getReportsByOrg(
          user.organizationId
        );
      }

      return await impactReportService.getAll();
    } catch (error) {
      return thunkAPI.rejectWithValue(
        error.response?.data || error
      );
    }
  }
);

export const submitImpactReport = createAsyncThunk(
  "impactReports/submitImpactReport",
  async (data, thunkAPI) => {
    try {
      return await impactReportService.submit(data);
    } catch (error) {
      return thunkAPI.rejectWithValue(
        error.response?.data || error
      );
    }
  }
);

const impactReportSlice = createSlice({
  name: "impactReports",
  initialState,

  reducers: {},

  extraReducers: (builder) => {
    builder
      .addCase(fetchImpactReports.pending, (state) => {
        state.loading = true;
        state.error = null;
      })

      .addCase(fetchImpactReports.fulfilled, (state, action) => {
        state.loading = false;
        state.items =
          action.payload.content || action.payload || [];
        state.totalPages = action.payload.totalPages || 0;
      })

      .addCase(fetchImpactReports.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      .addCase(submitImpactReport.fulfilled, (state, action) => {
        state.items.unshift(action.payload);
      });
  },
});

export default impactReportSlice.reducer;