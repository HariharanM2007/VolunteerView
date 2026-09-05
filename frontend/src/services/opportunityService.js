import api from './api';

const opportunityService = {
  getAll: async (page = 0, size = 10) => {
    const response = await api.get(`/opportunities?page=${page}&size=${size}`);
    return response ? response.data : { content: [], totalPages: 0 };
  },
  getById: async (id) => {
    const response = await api.get(`/opportunities/${id}`);
    return response ? response.data : null;
  },
  create: async (data) => {
    const response = await api.post('/opportunities', data);
    return response ? response.data : null;
  },
  publish: async (id) => {
    const response = await api.patch(`/opportunities/${id}/publish`);
    return response ? response.data : null;
  },
};

export default opportunityService;