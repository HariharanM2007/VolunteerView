import { configureStore } from "@reduxjs/toolkit";

import authReducer from "./slices/authSlice";
import organizationReducer from "./slices/organizationSlice";
import userReducer from "./slices/userSlice";

export const store = configureStore({
  reducer: {
    auth: authReducer,
    organizations: organizationReducer,
    users: userReducer,
  },
});