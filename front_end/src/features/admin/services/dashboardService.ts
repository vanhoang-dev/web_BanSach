import api from '@/services/api/axiosClient';
import { unwrapApiData } from '@/services/api/response';

export interface AdminDashboardStats {
  totalOrders?: number;
  totalPaidPayments?: number;
  totalRevenue?: number;
  totalBooks?: number;
  totalUsers?: number;
  totalBooksSold?: number;
  recentOrders?: any[];
}

const dashboardService = {
  getStats: async (): Promise<AdminDashboardStats> =>
    unwrapApiData<AdminDashboardStats>(await api.get('/admin/dashboard')) || {},
};

export default dashboardService;
