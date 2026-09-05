import api from './api';

const opportunityService = {
  getAll: async (page = 0, size = 10) => {
    const response = await api.get(`/opportunities?page=${page}&size=${size}`);
    return response.data;
  },
  getById: async (id) => {
    const response = await api.get(`/opportunities/${id}`);
    return response.data;
  },
  create: async (data) => {
    const response = await api.post('/opportunities', data);
    return response.data;
  },
  publish: async (id) => {
    const response = await api.patch(`/opportunities/${id}/publish`);
    return response.data;
  },
};

export default opportunityService;