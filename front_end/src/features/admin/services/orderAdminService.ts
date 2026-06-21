import api from '@/services/api/axiosClient';
import { unwrapApiData } from '@/services/api/response';
import { withPage } from '@/features/admin/services/pageResponse';

const orderAdminService = {
  getAll: async (page = 0, size = 10) => withPage(await api.get(`/admin/orders?page=${page}&size=${size}`)),
  getById: async (id: number) => unwrapApiData(await api.get(`/admin/orders/${id}`)),
  updateStatus: async (id: number, status: string) =>
    unwrapApiData(await api.put(`/admin/orders/${id}/status`, { status })),
  cancel: async (id: number) => api.put(`/admin/orders/${id}/cancel`, {}),
};

export default orderAdminService;
