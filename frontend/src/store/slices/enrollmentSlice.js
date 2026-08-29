import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import enrollmentService from "../../services/enrollmentService";

const initialState = {
  enrollments: [],
  loading: false,
  error: null,
  totalPages: 0,
  filterStatus: "ALL",
};

export const fetchEnrollments = createAsyncThunk(
  "enrollments/fetchEnrollments",
  async (_, thunkAPI) => {
    try {
      const user = thunkAPI.getState().auth.user;

      if (user?.role === "VOLUNTEER") {
        return await enrollmentService.getMyEnrollments();
      }

      if (user?.role === "ORGANIZATION_COORDINATOR") {
        if (!user.organizationId) {
          return thunkAPI.rejectWithValue({
            message: "You are not assigned to any organization.",
          });
        }

        return await enrollmentService.getEnrollmentsByOrg(
          user.organizationId
        );
      }

      return await enrollmentService.getAll();
    } catch (error) {
      return thunkAPI.rejectWithValue(
        error.response?.data || error
      );
    }
  }
);

export const enrollInOpportunity = createAsyncThunk(
  "enrollments/enrollInOpportunity",
  async (data, thunkAPI) => {
    try {
      return await enrollmentService.enroll(data);
    } catch (error) {
      return thunkAPI.rejectWithValue(
        error.response?.data || error
      );
    }
  }
);

export const approveEnrollment = createAsyncThunk(
  "enrollments/approveEnrollment",
  async (id, thunkAPI) => {
    try {
      return await enrollmentService.approve(id);
    } catch (error) {
      return thunkAPI.rejectWithValue(
        error.response?.data || error
      );
    }
  }
);

export const rejectEnrollment = createAsyncThunk(
  "enrollments/rejectEnrollment",
  async (id, thunkAPI) => {
    try {
      return await enrollmentService.reject(id);
    } catch (error) {
      return thunkAPI.rejectWithValue(
        error.response?.data || error
      );
    }
  }
);

export const checkInEnrollment = createAsyncThunk(
  "enrollments/checkInEnrollment",
  async (id, thunkAPI) => {
    try {
      return await enrollmentService.checkIn(id);
    } catch (error) {
      return thunkAPI.rejectWithValue(
        error.response?.data || error
      );
    }
  }
);

export const completeEnrollment = createAsyncThunk(
  "enrollments/completeEnrollment",
  async ({ id, hours }, thunkAPI) => {
    try {
      return await enrollmentService.complete(id, hours);
    } catch (error) {
      return thunkAPI.rejectWithValue(
        error.response?.data || error
      );
    }
  }
);

const replaceEnrollment = (state, action) => {
  const index = state.enrollments.findIndex(
    (item) => item.id === action.payload.id
  );

  if (index !== -1) {
    state.enrollments[index] = action.payload;
  }
};

const enrollmentSlice = createSlice({
  name: "enrollments",
  initialState,

  reducers: {
    setFilterStatus: (state, action) => {
      state.filterStatus = action.payload;
    },
  },

  extraReducers: (builder) => {
    builder
      .addCase(fetchEnrollments.pending, (state) => {
        state.loading = true;
        state.error = null;
      })

      .addCase(fetchEnrollments.fulfilled, (state, action) => {
        state.loading = false;
        state.enrollments =
          action.payload.content || action.payload || [];
        state.totalPages = action.payload.totalPages || 0;
      })

      .addCase(fetchEnrollments.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      .addCase(enrollInOpportunity.fulfilled, (state, action) => {
        state.enrollments.unshift(action.payload);
      })

      .addCase(approveEnrollment.fulfilled, replaceEnrollment)
      .addCase(rejectEnrollment.fulfilled, replaceEnrollment)
      .addCase(checkInEnrollment.fulfilled, replaceEnrollment)
      .addCase(completeEnrollment.fulfilled, replaceEnrollment);
  },
});

export const { setFilterStatus } = enrollmentSlice.actions;

export default enrollmentSlice.reducer;