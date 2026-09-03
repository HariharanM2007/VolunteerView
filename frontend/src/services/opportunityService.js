import api from "./api";

// Get all opportunities
const getAll = async (page = 0, size = 10) => {
  const response = await api.get(
    `/opportunities?page=${page}&size=${size}`
  );

  return response.data;
};

// Get a single opportunity
const getById = async (id) => {
  const response = await api.get(`/opportunities/${id}`);

  return response.data;
};

// Get opportunities belonging to an organization
const getByOrganization = async (organizationId) => {
  const response = await api.get(
    `/opportunities/organization/${organizationId}`
  );

  return response.data;
};

// Create opportunity
const create = async (data) => {
  const response = await api.post("/opportunities", data);

  return response.data;
};

// Update opportunity
const update = async (id, data) => {
  const response = await api.put(
    `/opportunities/${id}`,
    data
  );

  return response.data;
};

// Publish DRAFT opportunity
const publish = async (id) => {
  const response = await api.patch(
    `/opportunities/${id}/publish`
  );

  return response.data;
};

// Close OPEN opportunity
const close = async (id) => {
  const response = await api.patch(
    `/opportunities/${id}/close`
  );

  return response.data;
};

// Complete CLOSED opportunity
const complete = async (id) => {
  const response = await api.patch(
    `/opportunities/${id}/complete`
  );

  return response.data;
};

// Delete opportunity
const remove = async (id) => {
  const response = await api.delete(
    `/opportunities/${id}`
  );

  return response.data;
};

const opportunityService = {
  getAll,
  getById,
  getByOrganization,
  create,
  update,
  publish,
  close,
  complete,
  remove,
};

export default opportunityService;