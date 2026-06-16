import api from '@/services/api/axiosClient';
import { unwrapApiData } from '@/services/api/response';

export interface InventoryInfo {
  id?: number;
  bookId?: number;
  bookTitle?: string;
  quantity?: number;
  stock?: number;
  availableQuantity?: number;
}

const inventoryService = {
  getBookInventory: async (bookId: number): Promise<InventoryInfo> =>
    unwrapApiData(await api.get(`/user/inventory/book/${bookId}`)),
};

export default inventoryService;
