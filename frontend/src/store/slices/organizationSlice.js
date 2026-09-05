import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import organizationService from '../../services/organizationService';

export const fetchOrganizations = createAsyncThunk(
  'organizations/fetchOrganizations',
  async ({ page = 0, size = 10 } = {}, { rejectWithValue }) => {
    try {
      const data = await organizationService.getAll(page, size);
      return data;
    } catch (err) {
      return rejectWithValue(err.response?.data || { message: err.message });
    }
  }
);

export const fetchOrganizationById = createAsyncThunk(
  'organizations/fetchOrganizationById',
  async (id, { rejectWithValue }) => {
    try {
      const data = await organizationService.getById(id);
      return data;
    } catch (err) {
      return rejectWithValue(err.response?.data || { message: err.message });
    }
  }
);

export const createOrganization = createAsyncThunk(
  'organizations/createOrganization',
  async (orgData, { rejectWithValue }) => {
    try {
      const data = await organizationService.create(orgData);
      return data;
    } catch (err) {
      return rejectWithValue(err.response?.data || { message: err.message });
    }
  }
);

export const approveOrganization = createAsyncThunk(
  'organizations/approveOrganization',
  async (id, { rejectWithValue }) => {
    try {
      const data = await organizationService.approve(id);
      return data;
    } catch (err) {
      return rejectWithValue(err.response?.data || { message: err.message });
    }
  }
);

const initialState = {
  items: [],
  selectedItem: null,
  loading: false,
  error: null,
  totalPages: 0,
  totalElements: 0,
};

const organizationSlice = createSlice({
  name: 'organizations',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      // fetchOrganizations
      .addCase(fetchOrganizations.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchOrganizations.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload.content || action.payload || [];
        state.totalPages = action.payload.totalPages || 0;
        state.totalElements = action.payload.totalElements || 0;
      })
      .addCase(fetchOrganizations.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // fetchOrganizationById
      .addCase(fetchOrganizationById.fulfilled, (state, action) => {
        state.selectedItem = action.payload;
      })
      // createOrganization
      .addCase(createOrganization.fulfilled, (state, action) => {
        state.items.unshift(action.payload);
      })
      // approveOrganization
      .addCase(approveOrganization.fulfilled, (state, action) => {
        const index = state.items.findIndex((org) => org.id === action.payload.id);
        if (index !== -1) {
          state.items[index] = action.payload;
        }
      });
  },
});

export default organizationSlice.reducer;