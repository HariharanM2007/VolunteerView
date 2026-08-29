import api from "./api";

const getAll = async (page = 0, size = 10) => {
  const response = await api.get(
    `/impact-reports?page=${page}&size=${size}`
  );

  return response.data;
};

const submit = async (data) => {
  const response = await api.post("/impact-reports", data);
  return response.data;
};

const getMyReports = async (page = 0, size = 10) => {
  const response = await api.get(
    `/impact-reports/my?page=${page}&size=${size}`
  );

  return response.data;
};

const getReportsByOrg = async (orgId) => {
  const response = await api.get(
    `/impact-reports/organization/${orgId}`
  );

  return response.data;
};

const getStats = async () => {
  const response = await api.get("/dashboard/stats");
  return response.data;
};

const impactReportService = {
  getAll,
  submit,
  getMyReports,
  getReportsByOrg,
  getStats,
};

export default impactReportService;