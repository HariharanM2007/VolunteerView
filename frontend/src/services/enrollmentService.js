import api from "./api";

const getAll = async (page = 0, size = 10) => {
  const response = await api.get(
    `/enrollments?page=${page}&size=${size}`
  );

  return response.data;
};

const getMyEnrollments = async (page = 0, size = 10) => {
  const response = await api.get(
    `/enrollments/my?page=${page}&size=${size}`
  );

  return response.data;
};

const getById = async (id) => {
  const response = await api.get(`/enrollments/${id}`);
  return response.data;
};

const getEnrollmentsByOrg = async (organizationId) => {
  const response = await api.get(
    `/enrollments/organization/${organizationId}`
  );

  return response.data;
};

const enroll = async (data) => {
  const response = await api.post("/enrollments", data);
  return response.data;
};

const approve = async (id) => {
  const response = await api.patch(`/enrollments/${id}/approve`);
  return response.data;
};

const reject = async (id) => {
  const response = await api.patch(`/enrollments/${id}/reject`);
  return response.data;
};

const checkIn = async (id) => {
  const response = await api.patch(`/enrollments/${id}/check-in`);
  return response.data;
};

const complete = async (id, hours) => {
  const response = await api.patch(`/enrollments/${id}/complete`, {
    hours,
  });

  return response.data;
};

const enrollmentService = {
  getAll,
  getMyEnrollments,
  getById,
  getEnrollmentsByOrg,
  enroll,
  approve,
  reject,
  checkIn,
  complete,
};

export default enrollmentService;