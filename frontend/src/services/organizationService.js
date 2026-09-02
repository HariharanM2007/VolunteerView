import api from "./api";

const organizationService = {
  getAll: async (page = 0, size = 10) => {
    const response = await api.get(
      `/organizations?page=${page}&size=${size}`
    );

    return response.data;
  },

  getById: async (id) => {
    const response = await api.get(
      `/organizations/${id}`
    );

    return response.data;
  },

  create: async (data) => {
    const response = await api.post(
      "/organizations",
      data
    );

    return response.data;
  },

  approve: async (id) => {
    const response = await api.patch(
      `/organizations/${id}/approve`
    );

    return response.data;
  },
};

export default organizationService;