import api from '@/services/api/axiosClient';
import { unwrapApiData } from '@/services/api/response';
import { withPage } from '@/features/admin/services/pageResponse';

const userAdminService = {
  getAll: async (page = 0, size = 10) => withPage(await api.get(`/user_for_admin/all?page=${page}&size=${size}`)),
  getById: async (id: number) => unwrapApiData(await api.get(`/user_for_admin/user/${id}`)),
  update: async (id: number, userData: any) =>
    unwrapApiData(await api.put(`/user_for_admin/user/${id}`, userData)),
  remove: async (id: number) => api.delete(`/user_for_admin/user/delete/${id}`),
};

export default userAdminService;
