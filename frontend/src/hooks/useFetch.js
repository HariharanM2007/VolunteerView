import { useState, useCallback } from "react";
import api from "../services/api";

const useFetch = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchData = useCallback(async (url, options = {}) => {
    setLoading(true);
    setError(null);

    try {
      const response = await api({
        url,
        ...options,
      });

      setData(response.data);
      return response.data;
    } catch (err) {
      setError(err.response?.data || err);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  return {
    data,
    loading,
    error,
    fetchData,
  };
};

export default useFetch;