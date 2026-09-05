import api from './api';

const impactReportService = {
  getAll: async (page = 0, size = 10) => {
    const response = await api.get(`/impact-reports?page=${page}&size=${size}`);
    return response.data;
  },
  submit: async (data) => {
    const response = await api.post('/impact-reports', data);
    return response.data;
  },
  getMyReports: async (page = 0, size = 10) => {
    const response = await api.get(`/impact-reports/my?page=${page}&size=${size}`);
    return response.data;
  },
  getReportsByOrg: async (orgId) => {
    const response = await api.get(`/impact-reports/organization/${orgId}`);
    return response.data;
  },
  getStats: async () => {
    const response = await api.get('/dashboard/stats');
    return response.data;
  },
};

export default impactReportService;