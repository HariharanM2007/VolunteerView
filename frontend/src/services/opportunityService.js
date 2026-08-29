import api from "./api";

const getAll = async (page = 0, size = 10) => {
  const response = await api.get(
    `/opportunities?page=${page}&size=${size}`
  );

  return response.data;
};

const getById = async (id) => {
  const response = await api.get(`/opportunities/${id}`);
  return response.data;
};

const create = async (data) => {
  const response = await api.post("/opportunities", data);
  return response.data;
};

const publish = async (id) => {
  const response = await api.patch(`/opportunities/${id}/publish`);
  return response.data;
};

const opportunityService = {
  getAll,
  getById,
  create,
  publish,
};

export default opportunityService;