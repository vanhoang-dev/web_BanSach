import api from '@/services/api/axiosClient';
import { unwrapApiData } from '@/services/api/response';

const inventoryAdminService = {
  getAll: async (page = 0, size = 10) => {
    const response = await api.get('/admin/inventory');
    const inventory = unwrapApiData<any[]>(response) || [];
    const start = page * size;
    const content = inventory.slice(start, start + size);
    return {
      data: {
        content,
        totalElements: inventory.length,
        totalPages: Math.ceil(inventory.length / size),
        number: page,
        size,
      },
    };
  },
  setQuantity: async (id: number, quantity: number) =>
    unwrapApiData(await api.put(`/admin/inventory/${id}/set/${quantity}`)),
  adjust: async (id: number, delta: number) =>
    unwrapApiData(await api.post(`/admin/inventory/${id}/adjust/${delta}`, {})),
  reconcile: async (id: number, quantity: number) =>
    unwrapApiData(await api.put(`/admin/inventory/${id}/reconcile/${quantity}`, {})),
};

export default inventoryAdminService;
