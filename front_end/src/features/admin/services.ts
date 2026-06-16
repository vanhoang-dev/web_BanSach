import api from '@/services/api/axiosClient';
import { unwrapApiData, unwrapPage } from '@/services/api/response';

export interface AdminDashboardStats {
  totalOrders?: number;
  totalPaidPayments?: number;
  totalRevenue?: number;
  totalBooks?: number;
  totalUsers?: number;
  totalBooksSold?: number;
  recentOrders?: any[];
}

const withPage = (response: any, mapper = (item: any) => item) => {
  const page = unwrapPage<any>(response);
  return {
    data: {
      ...page,
      content: page.content.map(mapper),
    },
  };
};

const normalizeBookAdminFormData = (bookData: any) => {
  if (bookData instanceof FormData) return bookData;

  const formData = new FormData();
  const file = bookData.coverImageFile || bookData.file || bookData.coverImage;
  const payload = {
    title: bookData.title,
    isbn: bookData.isbn,
    publisher: bookData.publisher,
    publicationYear: Number(bookData.publicationYear),
    price: Number(bookData.price),
    description: bookData.description || '',
    authorId: Number(bookData.authorId),
    categoryId: Number(bookData.categoryId),
    discountId: bookData.discountId ? Number(bookData.discountId) : null,
  };

  Object.entries(payload).forEach(([key, value]) => {
    if (value !== null && value !== undefined) {
      formData.append(key, String(value));
    }
  });
  if (file instanceof File) {
    formData.append('image', file);
  }
  return formData;
};

const adminService = {
  getDashboardStats: async (): Promise<AdminDashboardStats> => {
    const response = await api.get('/admin/dashboard');
    return unwrapApiData<AdminDashboardStats>(response) || {};
  },

  getOrders: async (page: number = 0, size: number = 10): Promise<any> => {
    const response = await api.get(`/admin/orders?page=${page}&size=${size}`);
    return withPage(response);
  },

  getOrderById: async (id: number): Promise<any> => unwrapApiData(await api.get(`/admin/orders/${id}`)),

  updateOrderStatus: async (id: number, status: string): Promise<any> =>
    unwrapApiData(await api.put(`/admin/orders/${id}/status`, { status })),

  cancelOrder: async (id: number): Promise<any> => api.put(`/admin/orders/${id}/cancel`, {}),

  getBooks: async (page: number = 0, size: number = 10): Promise<any> => {
    const response = await api.get(`/admin/books?page=${page}&size=${size}`);
    return withPage(response);
  },

  getBookById: async (id: number): Promise<any> => unwrapApiData(await api.get(`/admin/books/${id}`)),

  addBook: async (bookData: any): Promise<any> =>
    unwrapApiData(await api.post('/admin/books/create-book', normalizeBookAdminFormData(bookData), {
      headers: { 'Content-Type': 'multipart/form-data' },
    })),

  updateBook: async (id: number, bookData: any): Promise<any> =>
    unwrapApiData(await api.put(`/admin/books/update-book/${id}`, normalizeBookAdminFormData(bookData), {
      headers: { 'Content-Type': 'multipart/form-data' },
    })),

  deleteBook: async (id: number): Promise<any> => api.delete(`/admin/books/delete-book/${id}`),

  getUsers: async (page: number = 0, size: number = 10): Promise<any> => {
    const response = await api.get(`/user_for_admin/all?page=${page}&size=${size}`);
    return withPage(response);
  },

  getUserById: async (id: number): Promise<any> => unwrapApiData(await api.get(`/user_for_admin/user/${id}`)),

  updateUser: async (id: number, userData: any): Promise<any> =>
    unwrapApiData(await api.put(`/user_for_admin/user/${id}`, userData)),

  deleteUser: async (id: number): Promise<any> => api.delete(`/user_for_admin/user/delete/${id}`),

  getCategories: async (page: number = 0, size: number = 50): Promise<any> => {
    const response = await api.get(`/api/admin/categories?page=${page}&size=${size}`);
    return withPage(response);
  },

  addCategory: async (categoryData: any): Promise<any> =>
    unwrapApiData(await api.post('/api/admin/categories', categoryData)),

  updateCategory: async (id: number, categoryData: any): Promise<any> =>
    unwrapApiData(await api.put(`/api/admin/categories/${id}`, categoryData)),

  deleteCategory: async (id: number): Promise<any> => api.delete(`/api/admin/categories/${id}`),

  activateCategory: async (id: number): Promise<any> => api.put(`/api/admin/categories/${id}/activate`, {}),

  deactivateCategory: async (id: number): Promise<any> => api.put(`/api/admin/categories/${id}/deactivate`, {}),

  getVouchers: async (page: number = 0, size: number = 10): Promise<any> => {
    const response = await api.get(`/admin/vouchers?page=${page}&size=${size}`);
    return withPage(response);
  },

  getVoucherById: async (id: number): Promise<any> => unwrapApiData(await api.get(`/admin/vouchers/${id}`)),

  getExpiredVouchers: async (page: number = 0, size: number = 10): Promise<any> => {
    const response = await api.get(`/admin/vouchers/expired?page=${page}&size=${size}`);
    return withPage(response);
  },

  addVoucher: async (voucherData: any): Promise<any> =>
    unwrapApiData(await api.post('/admin/vouchers', {
      code: voucherData.code,
      discountPercent: Number(voucherData.discountPercent),
      maxDiscount: Number(voucherData.maxDiscount),
      quantity: Number(voucherData.quantity),
      expiredAt: voucherData.expiredAt,
    })),

  updateVoucher: async (id: number, voucherData: any): Promise<any> =>
    unwrapApiData(await api.put(`/admin/vouchers/${id}`, voucherData)),

  deleteVoucher: async (id: number): Promise<any> => api.delete(`/admin/vouchers/${id}`),

  getAuthors: async (page: number = 0, size: number = 50): Promise<any> => {
    const response = await api.get(`/admin/authors?page=${page}&size=${size}`);
    return withPage(response);
  },

  addAuthor: async (authorData: any): Promise<any> =>
    unwrapApiData(await api.post('/admin/authors', {
      authorName: authorData.authorName || authorData.name,
      biography: authorData.biography || '',
    })),

  updateAuthor: async (id: number, authorData: any): Promise<any> =>
    unwrapApiData(await api.put(`/admin/authors/${id}`, authorData)),

  deleteAuthor: async (id: number): Promise<any> => api.delete(`/admin/authors/${id}`),

  getInventory: async (page: number = 0, size: number = 10): Promise<any> => {
    const response = await api.get(`/admin/inventory?page=${page}&size=${size}`);
    return withPage(response);
  },

  updateInventory: async (id: number, quantity: number): Promise<any> =>
    unwrapApiData(await api.put(`/admin/inventory/${id}/set/${quantity}`)),

  adjustInventory: async (id: number, delta: number): Promise<any> =>
    unwrapApiData(await api.post(`/admin/inventory/${id}/adjust/${delta}`, {})),

  reconcileInventory: async (id: number, quantity: number): Promise<any> =>
    unwrapApiData(await api.put(`/admin/inventory/${id}/reconcile/${quantity}`, {})),
};

export default adminService;
