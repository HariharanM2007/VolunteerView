import api from './api';

const userService = {
  getAll: async () => {
    const response = await api.get('/users');
    return response.data;
  },
  updateStatus: async (id, status) => {
    const response = await api.patch(`/users/${id}/status`, { status });
    return response.data;
  },
  assignOrganization: async (userId, organizationId) => {
    const response = await api.patch(`/users/${userId}/assign-organization/${organizationId}`);
    return response.data;
  },
};

export default userService;
