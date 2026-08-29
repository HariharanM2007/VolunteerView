import { configureStore } from "@reduxjs/toolkit";

import authReducer from "./slices/authSlice";
import organizationReducer from "./slices/organizationSlice";
import opportunityReducer from "./slices/opportunitySlice";
import enrollmentReducer from "./slices/enrollmentSlice";
import impactReportReducer from "./slices/impactReportSlice";
import notificationReducer from "./slices/notificationSlice";

export const store = configureStore({
  reducer: {
    auth: authReducer,
    organizations: organizationReducer,
    opportunities: opportunityReducer,
    enrollments: enrollmentReducer,
    impactReports: impactReportReducer,
    notifications: notificationReducer,
  },
});

export default store;