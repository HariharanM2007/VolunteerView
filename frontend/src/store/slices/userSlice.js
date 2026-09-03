import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import userService from "../../services/userService";

const initialState = {
  items: [],
  loading: false,
  error: null,
};

export const fetchUsers = createAsyncThunk(
  "users/fetchUsers",
  async (_, thunkAPI) => {
    try {
      return await userService.getAll();
    } catch (error) {
      return thunkAPI.rejectWithValue(
        error.response?.data || {
          message: "Failed to fetch users.",
        }
      );
    }
  }
);

export const updateUserStatus = createAsyncThunk(
  "users/updateStatus",
  async ({ id, status }, thunkAPI) => {
    try {
      return await userService.updateStatus(id, status);
    } catch (error) {
      return thunkAPI.rejectWithValue(
        error.response?.data || {
          message: "Failed to update user status.",
        }
      );
    }
  }
);

export const assignUserOrganization = createAsyncThunk(
  "users/assignOrganization",
  async ({ userId, organizationId }, thunkAPI) => {
    try {
      return await userService.assignOrganization(
        userId,
        organizationId
      );
    } catch (error) {
      return thunkAPI.rejectWithValue(
        error.response?.data || {
          message: "Failed to assign organization.",
        }
      );
    }
  }
);

const userSlice = createSlice({
  name: "users",

  initialState,

  reducers: {
    clearUserError: (state) => {
      state.error = null;
    },
  },

  extraReducers: (builder) => {
    builder

      // Fetch users
      .addCase(fetchUsers.pending, (state) => {
        state.loading = true;
        state.error = null;
      })

      .addCase(fetchUsers.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload;
      })

      .addCase(fetchUsers.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Update status
      .addCase(updateUserStatus.pending, (state) => {
        state.loading = true;
        state.error = null;
      })

      .addCase(updateUserStatus.fulfilled, (state, action) => {
        state.loading = false;

        const updatedUser = action.payload;

        const index = state.items.findIndex(
          (user) => user.id === updatedUser.id
        );

        if (index !== -1) {
          state.items[index] = updatedUser;
        }
      })

      .addCase(updateUserStatus.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Assign organization
      .addCase(assignUserOrganization.pending, (state) => {
        state.loading = true;
        state.error = null;
      })

      .addCase(assignUserOrganization.fulfilled, (state, action) => {
        state.loading = false;

        const updatedUser = action.payload;

        const index = state.items.findIndex(
          (user) => user.id === updatedUser.id
        );

        if (index !== -1) {
          state.items[index] = updatedUser;
        }
      })

      .addCase(assignUserOrganization.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export const { clearUserError } = userSlice.actions;

export default userSlice.reducer;