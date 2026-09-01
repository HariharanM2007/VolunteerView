import axios from "axios";

const API_BASE_URL = "http://localhost:8081/api";

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// Add access token to every request
api.interceptors.request.use(
  (config) => {
    const storedUser = localStorage.getItem("user");

    if (storedUser) {
      try {
        const user = JSON.parse(storedUser);

        if (user?.accessToken) {
          config.headers.Authorization = `Bearer ${user.accessToken}`;
        }
      } catch (error) {
        console.error("Failed to parse stored user:", error);
      }
    }

    return config;
  },
  (error) => Promise.reject(error)
);

// Handle expired access token
api.interceptors.response.use(
  (response) => response,

  async (error) => {
    const originalRequest = error.config;

    if (
      error.response?.status === 401 &&
      !originalRequest?._retry
    ) {
      originalRequest._retry = true;

      try {
        const storedUser = localStorage.getItem("user");

        if (!storedUser) {
          throw new Error("No authenticated user found.");
        }

        const user = JSON.parse(storedUser);

        if (!user?.refreshToken) {
          throw new Error("No refresh token available.");
        }

        const response = await axios.post(
          `${API_BASE_URL}/auth/refresh`,
          {
            refreshToken: user.refreshToken,
          }
        );

        const newAccessToken = response.data.accessToken;

        const updatedUser = {
          ...user,
          accessToken: newAccessToken,
        };

        localStorage.setItem(
          "user",
          JSON.stringify(updatedUser)
        );

        originalRequest.headers.Authorization =
          `Bearer ${newAccessToken}`;

        return api(originalRequest);
      } catch (refreshError) {
        localStorage.removeItem("user");

        window.location.href = "/login";

        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default api;