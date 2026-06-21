import api from '@/services/api/axiosClient';
import { unwrapApiData } from '@/services/api/response';
import { withPage } from '@/features/admin/services/pageResponse';

const authorAdminService = {
  getAll: async (page = 0, size = 50) => withPage(await api.get(`/admin/authors?page=${page}&size=${size}`)),
  create: async (data: any) => unwrapApiData(await api.post('/admin/authors', {
    authorName: data.authorName || data.name,
    biography: data.biography || '',
  })),
  update: async (id: number, data: any) => unwrapApiData(await api.put(`/admin/authors/${id}`, data)),
  remove: async (id: number) => api.delete(`/admin/authors/${id}`),
};

export default authorAdminService;
