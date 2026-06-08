import type { AxiosInstance } from 'axios';

import { tokenStorage } from '@/services/storage/tokenStorage';

export function attachAuthInterceptor(api: AxiosInstance) {
  api.interceptors.request.use((config) => {
    const token = tokenStorage.getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  });
}
