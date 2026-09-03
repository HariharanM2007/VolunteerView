import api from "./api";

const getAll = async () => {
  const response = await api.get("/users");
  return response.data;
};

const updateStatus = async (id, status) => {
  const response = await api.patch(`/users/${id}/status`, {
    status,
  });
  return response.data;
};

const assignOrganization = async (userId, organizationId) => {
  const response = await api.patch(
    `/users/${userId}/assign-organization/${organizationId}`
  );
  return response.data;
};

const userService = {
  getAll,
  updateStatus,
  assignOrganization,
};

export default userService;