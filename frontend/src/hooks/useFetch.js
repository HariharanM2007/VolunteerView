import { useState, useEffect } from 'react';
import api from '../services/api';

const useFetch = (url, options = {}) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let isMounted = true;
    if (!url) return;

    setLoading(true);
    setError(null);

    api.get(url, options)
      .then((response) => {
        if (isMounted) {
          setData(response.data);
          setLoading(false);
        }
      })
      .catch((err) => {
        if (isMounted) {
          setError(err.response?.data || err.message);
          setLoading(false);
        }
      });

    return () => {
      isMounted = false;
    };
  }, [url]);

  return { data, loading, error };
};

export default useFetch;