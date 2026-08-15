import axios from 'axios';

import { env } from '@/config/env';
import { tokenStorage } from '@/services/storage/tokenStorage';

const api = axios.create({
  baseURL: env.apiBaseUrl,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
});

api.interceptors.request.use(
  (config) => {
    const token = tokenStorage.getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const hadToken = !!tokenStorage.getToken();
    const status = error.response?.status;
    if (status === 401 && hadToken) {
      tokenStorage.clear();
      window.location.href = '/401';
    } else if (status === 403) {
      window.location.href = '/403';
    } else if (!error.response) {
      window.location.href = '/network-error';
    }
    return Promise.reject(error.response?.data || error.message);
  }
);

export default api;
