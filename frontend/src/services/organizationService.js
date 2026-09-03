import api from "./api";

const getAll = async (page = 0, size = 10) => {
  const response = await api.get(
    `/organizations?page=${page}&size=${size}`
  );

  return response.data;
};

const getById = async (id) => {
  const response = await api.get(`/organizations/${id}`);

  return response.data;
};

const create = async (data) => {
  const response = await api.post("/organizations", data);

  return response.data;
};

const approve = async (id) => {
  const response = await api.patch(
    `/organizations/${id}/approve`
  );

  return response.data;
};

const organizationService = {
  getAll,
  getById,
  create,
  approve,
};

export default organizationService;