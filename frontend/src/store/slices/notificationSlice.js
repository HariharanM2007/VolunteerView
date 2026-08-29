import { createSlice } from "@reduxjs/toolkit";

const initialState = {
  list: [],
};

const notificationSlice = createSlice({
  name: "notifications",
  initialState,

  reducers: {
    addNotification: (state, action) => {
      state.list.push({
        id: action.payload.id || Date.now(),
        message: action.payload.message,
        type: action.payload.type || "info",
        duration: action.payload.duration || 5000,
      });
    },

    removeNotification: (state, action) => {
      state.list = state.list.filter(
        (note) => note.id !== action.payload
      );
    },
  },
});

export const { addNotification, removeNotification } =
  notificationSlice.actions;

export const notify =
  (message, type = "info", duration = 5000) =>
  (dispatch) => {
    const id = Date.now();

    dispatch(
      addNotification({
        id,
        message,
        type,
        duration,
      })
    );

    setTimeout(() => {
      dispatch(removeNotification(id));
    }, duration);
  };

export default notificationSlice.reducer;