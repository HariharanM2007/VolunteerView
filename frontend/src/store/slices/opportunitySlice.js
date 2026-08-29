import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import opportunityService from "../../services/opportunityService";

const initialState = {
  items: [],
  selectedItem: null,
  loading: false,
  error: null,
  totalPages: 0,
  searchQuery: "",
};

export const fetchOpportunities = createAsyncThunk(
  "opportunities/fetchOpportunities",
  async ({ page = 0, size = 10 } = {}, thunkAPI) => {
    try {
      return await opportunityService.getAll(page, size);
    } catch (error) {
      return thunkAPI.rejectWithValue(
        error.response?.data || error
      );
    }
  }
);

export const fetchOpportunityById = createAsyncThunk(
  "opportunities/fetchOpportunityById",
  async (id, thunkAPI) => {
    try {
      return await opportunityService.getById(id);
    } catch (error) {
      return thunkAPI.rejectWithValue(
        error.response?.data || error
      );
    }
  }
);

export const createOpportunity = createAsyncThunk(
  "opportunities/createOpportunity",
  async (data, thunkAPI) => {
    try {
      return await opportunityService.create(data);
    } catch (error) {
      return thunkAPI.rejectWithValue(
        error.response?.data || error
      );
    }
  }
);

export const publishOpportunity = createAsyncThunk(
  "opportunities/publishOpportunity",
  async (id, thunkAPI) => {
    try {
      return await opportunityService.publish(id);
    } catch (error) {
      return thunkAPI.rejectWithValue(
        error.response?.data || error
      );
    }
  }
);

const opportunitySlice = createSlice({
  name: "opportunities",
  initialState,

  reducers: {
    setSearchQuery: (state, action) => {
      state.searchQuery = action.payload;
    },
  },

  extraReducers: (builder) => {
    builder
      .addCase(fetchOpportunities.pending, (state) => {
        state.loading = true;
        state.error = null;
      })

      .addCase(fetchOpportunities.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload.content || [];
        state.totalPages = action.payload.totalPages || 0;
      })

      .addCase(fetchOpportunities.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      .addCase(createOpportunity.fulfilled, (state, action) => {
        state.items.unshift(action.payload);
      })

      .addCase(publishOpportunity.fulfilled, (state, action) => {
        const index = state.items.findIndex(
          (item) => item.id === action.payload.id
        );

        if (index !== -1) {
          state.items[index] = action.payload;
        }
      });
  },
});

export const { setSearchQuery } = opportunitySlice.actions;

export default opportunitySlice.reducer;