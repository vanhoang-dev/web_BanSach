import api from '@/services/api/axiosClient';
import { unwrapApiData } from '@/services/api/response';
import { withPage } from '@/features/admin/services/pageResponse';

const voucherAdminService = {
  getAll: async (page = 0, size = 10) => withPage(await api.get(`/admin/vouchers?page=${page}&size=${size}`)),
  getById: async (id: number) => unwrapApiData(await api.get(`/admin/vouchers/${id}`)),
  getExpired: async (page = 0, size = 10) => withPage(await api.get(`/admin/vouchers/expired?page=${page}&size=${size}`)),
  create: async (data: any) => unwrapApiData(await api.post('/admin/vouchers', {
    code: data.code,
    discountPercent: Number(data.discountPercent),
    maxDiscount: Number(data.maxDiscount),
    quantity: Number(data.quantity),
    expiredAt: data.expiredAt,
  })),
  update: async (id: number, data: any) => unwrapApiData(await api.put(`/admin/vouchers/${id}`, data)),
  remove: async (id: number) => api.delete(`/admin/vouchers/${id}`),
};

export default voucherAdminService;
