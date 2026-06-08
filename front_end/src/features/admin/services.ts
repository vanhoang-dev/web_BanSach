import api from '@/services/api/axiosClient';

export interface AdminDashboardStats {
  totalOrders?: number;
  totalRevenue?: number;
  totalBooks?: number;
  totalUsers?: number;
  recentOrders?: any[];
}

const adminService = {
  // Lấy thống kê dashboard
  getDashboardStats: async (): Promise<any> => {
    try {
      const response = await api.get('/admin/dashboard');
      return response;
    } catch (error: any) {
      throw error;
    }
  },

  // Lấy danh sách đơn hàng
  getOrders: async (page: number = 0, size: number = 10): Promise<any> => {
    try {
      const response = await api.get(`/admin/orders?page=${page}&size=${size}`);
      return response;
    } catch (error: any) {
      throw error;
    }
  },

  // Lấy danh sách sách
  getBooks: async (page: number = 0, size: number = 10): Promise<any> => {
    try {
      const response = await api.get(`/admin/books?page=${page}&size=${size}`);
      return response;
    } catch (error: any) {
      throw error;
    }
  },

  // Thêm sách mới
  addBook: async (bookData: any): Promise<any> => {
    try {
      const response = await api.post('/admin/books/create-book', bookData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      return response;
    } catch (error: any) {
      throw error;
    }
  },

  // Cập nhật sách
  updateBook: async (id: number, bookData: any): Promise<any> => {
    try {
      const response = await api.put(`/admin/books/update-book/${id}`, bookData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      return response;
    } catch (error: any) {
      throw error;
    }
  },

  // Xóa sách
  deleteBook: async (id: number): Promise<any> => {
    try {
      const response = await api.delete(`/admin/books/delete-book/${id}`);
      return response;
    } catch (error: any) {
      throw error;
    }
  },

  // Lấy danh sách người dùng
  getUsers: async (page: number = 0, size: number = 10): Promise<any> => {
    try {
      const response = await api.get(`/user_for_admin/all?page=${page}&size=${size}`);
      return response;
    } catch (error: any) {
      throw error;
    }
  },

  // Lấy danh sách voucher
  getVouchers: async (): Promise<any> => {
    try {
      const response = await api.get('/admin/vouchers');
      return response;
    } catch (error: any) {
      throw error;
    }
  },

  // Thêm voucher
  addVoucher: async (voucherData: any): Promise<any> => {
    try {
      const response = await api.post('/admin/vouchers', voucherData);
      return response;
    } catch (error: any) {
      throw error;
    }
  },

  // Cập nhật trạng thái đơn hàng
  updateOrderStatus: async (id: number, status: string): Promise<any> => {
    try {
      const response = await api.put(`/admin/orders/${id}/status`, { status });
      return response;
    } catch (error: any) {
      throw error;
    }
  },

  // Lấy danh sách tác giả
  getAuthors: async (): Promise<any> => {
    try {
      const response = await api.get('/admin/authors');
      return response;
    } catch (error: any) {
      throw error;
    }
  },

  // Thêm tác giả
  addAuthor: async (authorData: any): Promise<any> => {
    try {
      const response = await api.post('/admin/authors', authorData);
      return response;
    } catch (error: any) {
      throw error;
    }
  },

  // Lấy thông tin quản lý kho
  getInventory: async (page: number = 0, size: number = 10): Promise<any> => {
    try {
      const response = await api.get(`/admin/inventory?page=${page}&size=${size}`);
      return response;
    } catch (error: any) {
      throw error;
    }
  },

  // Cập nhật kho
  updateInventory: async (id: number, quantity: number): Promise<any> => {
    try {
      const response = await api.put(`/admin/inventory/${id}/set/${quantity}`);
      return response;
    } catch (error: any) {
      throw error;
    }
  },
};

export default adminService;
