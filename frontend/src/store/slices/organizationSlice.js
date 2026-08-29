import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import organizationService from "../../services/organizationService";

const initialState = {
  items: [],
  selectedItem: null,
  loading: false,
  error: null,
  totalPages: 0,
  totalElements: 0,
};

export const fetchOrganizations = createAsyncThunk(
  "organizations/fetchOrganizations",
  async ({ page = 0, size = 10 } = {}, thunkAPI) => {
    try {
      return await organizationService.getAll(page, size);
    } catch (error) {
      return thunkAPI.rejectWithValue(
        error.response?.data || error
      );
    }
  }
);

export const fetchOrganizationById = createAsyncThunk(
  "organizations/fetchOrganizationById",
  async (id, thunkAPI) => {
    try {
      return await organizationService.getById(id);
    } catch (error) {
      return thunkAPI.rejectWithValue(
        error.response?.data || error
      );
    }
  }
);

export const createOrganization = createAsyncThunk(
  "organizations/createOrganization",
  async (data, thunkAPI) => {
    try {
      return await organizationService.create(data);
    } catch (error) {
      return thunkAPI.rejectWithValue(
        error.response?.data || error
      );
    }
  }
);

export const approveOrganization = createAsyncThunk(
  "organizations/approveOrganization",
  async (id, thunkAPI) => {
    try {
      return await organizationService.approve(id);
    } catch (error) {
      return thunkAPI.rejectWithValue(
        error.response?.data || error
      );
    }
  }
);

const organizationSlice = createSlice({
  name: "organizations",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchOrganizations.pending, (state) => {
        state.loading = true;
        state.error = null;
      })

      .addCase(fetchOrganizations.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload.content || [];
        state.totalPages = action.payload.totalPages || 0;
        state.totalElements = action.payload.totalElements || 0;
      })

      .addCase(fetchOrganizations.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      .addCase(fetchOrganizationById.fulfilled, (state, action) => {
        state.selectedItem = action.payload;
      })

      .addCase(createOrganization.fulfilled, (state, action) => {
        state.items.unshift(action.payload);
      })

      .addCase(approveOrganization.fulfilled, (state, action) => {
        const index = state.items.findIndex(
          (item) => item.id === action.payload.id
        );

        if (index !== -1) {
          state.items[index] = action.payload;
        }
      });
  },
});

export default organizationSlice.reducer;