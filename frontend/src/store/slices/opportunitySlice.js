import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import opportunityService from '../../services/opportunityService';

export const fetchOpportunities = createAsyncThunk(
  'opportunities/fetchOpportunities',
  async ({ page = 0, size = 10 } = {}, { rejectWithValue }) => {
    try {
      const data = await opportunityService.getAll(page, size);
      return data;
    } catch (err) {
      return rejectWithValue(err.response?.data || { message: err.message });
    }
  }
);

export const createOpportunity = createAsyncThunk(
  'opportunities/createOpportunity',
  async (oppData, { rejectWithValue }) => {
    try {
      const data = await opportunityService.create(oppData);
      return data;
    } catch (err) {
      return rejectWithValue(err.response?.data || { message: err.message });
    }
  }
);

export const publishOpportunity = createAsyncThunk(
  'opportunities/publishOpportunity',
  async (id, { rejectWithValue }) => {
    try {
      const data = await opportunityService.publish(id);
      return data;
    } catch (err) {
      return rejectWithValue(err.response?.data || { message: err.message });
    }
  }
);

export const deleteOpportunity = createAsyncThunk(
  'opportunities/deleteOpportunity',
  async (id, { rejectWithValue }) => {
    try {
      await opportunityService.delete(id);
      return id;
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
  searchQuery: '',
};

const opportunitySlice = createSlice({
  name: 'opportunities',
  initialState,
  reducers: {
    setSearchQuery: (state, action) => {
      state.searchQuery = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder
      // fetchOpportunities
      .addCase(fetchOpportunities.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchOpportunities.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload.content || action.payload || [];
        state.totalPages = action.payload.totalPages || 0;
      })
      .addCase(fetchOpportunities.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // createOpportunity
      .addCase(createOpportunity.fulfilled, (state, action) => {
        state.items.unshift(action.payload);
      })
      // publishOpportunity
      .addCase(publishOpportunity.fulfilled, (state, action) => {
        const index = state.items.findIndex((opp) => opp.id === action.payload.id);
        if (index !== -1) {
          state.items[index] = action.payload;
        }
      })
      .addCase(deleteOpportunity.fulfilled, (state, action) => {
        state.items = state.items.filter((opp) => opp.id !== action.payload);
      });
  },
});

export const { setSearchQuery } = opportunitySlice.actions;
export default opportunitySlice.reducer;