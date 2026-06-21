import api from '@/services/api/axiosClient';
import { unwrapApiData } from '@/services/api/response';
import { withPage } from '@/features/admin/services/pageResponse';

const categoryAdminService = {
  getAll: async (page = 0, size = 50) => withPage(await api.get(`/api/admin/categories?page=${page}&size=${size}`)),
  create: async (data: any) => unwrapApiData(await api.post('/api/admin/categories', data)),
  update: async (id: number, data: any) => unwrapApiData(await api.put(`/api/admin/categories/${id}`, data)),
  remove: async (id: number) => api.delete(`/api/admin/categories/${id}`),
  activate: async (id: number) => api.put(`/api/admin/categories/${id}/activate`, {}),
  deactivate: async (id: number) => api.put(`/api/admin/categories/${id}/deactivate`, {}),
};

export default categoryAdminService;
