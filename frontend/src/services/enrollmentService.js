import api from './api';

const enrollmentService = {
  getAll: async (page = 0, size = 10) => {
    const response = await api.get(`/enrollments?page=${page}&size=${size}`);
    return response.data;
  },
  getMyEnrollments: async (page = 0, size = 10) => {
    const response = await api.get(`/enrollments/my?page=${page}&size=${size}`);
    return response.data;
  },
  getEnrollmentsByOrg: async (orgId, page = 0, size = 10) => {
    const response = await api.get(`/enrollments/organization/${orgId}?page=${page}&size=${size}`);
    return response.data;
  },
  enroll: async (data) => {
    const response = await api.post('/enrollments/enroll', data);
    return response.data;
  },
  approve: async (id) => {
    const response = await api.patch(`/enrollments/${id}/approve`);
    return response.data;
  },
  reject: async (id) => {
    const response = await api.patch(`/enrollments/${id}/reject`);
    return response.data;
  },
  checkIn: async (id) => {
    const response = await api.patch(`/enrollments/${id}/check-in`);
    return response.data;
  },
  complete: async (id, hours) => {
    const response = await api.patch(`/enrollments/${id}/complete?hours=${hours}`);
    return response.data;
  },
};

export default enrollmentService;