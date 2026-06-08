import api from '@/services/api/axiosClient';

export interface OrderItem {
  id?: number;
  bookId: number;
  quantity: number;
  price: number;
  book?: {
    id: number;
    title: string;
    cover?: string;
  };
}

export interface Order {
  id?: number;
  orderCode?: string;
  items: OrderItem[];
  totalPrice: number;
  shippingAddress: string;
  phoneNumber: string;
  fullName: string;
  email?: string;
  shippingMethod?: string;
  shippingFee?: number;
  voucherCode?: string;
  voucherDiscount?: number;
  status?: 'PENDING' | 'CONFIRMED' | 'SHIPPING' | 'DELIVERED' | 'CANCELLED';
  paymentMethod?: 'COD' | 'BANK_TRANSFER' | 'SEPAY' | 'MOMO';
  paymentStatus?: 'UNPAID' | 'PAID';
  createdAt?: string;
  updatedAt?: string;
  notes?: string;
}

export interface OrderResponse {
  success: boolean;
  data?: Order;
  message?: string;
}

export interface OrdersListResponse {
  success: boolean;
  data?: {
    content: Order[];
    totalElements: number;
    totalPages: number;
    currentPage: number;
    pageSize: number;
  };
}

const orderService = {
  // Tạo đơn hàng
  createOrder: async (orderData: Order): Promise<any> => {
    try {
      const payload = {
        receiverName: orderData.fullName,
        receiverPhone: orderData.phoneNumber,
        shippingAddress: orderData.shippingAddress,
        shippingMethod: orderData.shippingMethod || 'STANDARD',
        shippingFee: 0,
        voucherCode: undefined,
      };
      const response = await api.post('/user/orders', payload);
      return response;
    } catch (error: any) {
      throw error;
    }
  },

  // Lấy danh sách đơn hàng của user
  getOrders: async (page: number = 0, size: number = 10): Promise<any> => {
    try {
      const response: any = await api.get(`/user/orders?page=${page}&size=${size}`);
      const pageData = response?.content ? response : response?.data?.content ? response.data : { content: [] };
      return {
        data: {
          ...pageData,
          content: (pageData.content || []).map((order: any) => ({
            id: order.id,
            items: order.items || [],
            totalPrice: Number(order.totalAmount || 0),
            status: order.status,
            fullName: order.receiverName,
            phoneNumber: order.receiverPhone,
            shippingAddress: order.shippingAddress,
            createdAt: order.orderDate,
          })),
        },
      };
    } catch (error: any) {
      throw error;
    }
  },

  // Lấy chi tiết đơn hàng
  getOrderById: async (id: number): Promise<any> => {
    try {
      const response: any = await api.get(`/user/orders/${id}`);
      const order = response?.data || response;
      return {
        id: order.id,
        items: order.items || [],
        totalPrice: Number(order.totalAmount || 0),
        status: order.status,
        fullName: order.receiverName,
        phoneNumber: order.receiverPhone,
        shippingAddress: order.shippingAddress,
        createdAt: order.orderDate,
      };
    } catch (error: any) {
      throw error;
    }
  },

  // Hủy đơn hàng
  cancelOrder: async (id: number): Promise<any> => {
    try {
      const response = await api.put(`/user/orders/${id}/cancel`, {});
      return response;
    } catch (error: any) {
      throw error;
    }
  },

  // Kiểm tra thanh toán
  checkPaymentStatus: async (orderId: number): Promise<any> => {
    try {
      const response = await api.get(`/api/payment/status/order/${orderId}`);
      return response;
    } catch (error: any) {
      throw error;
    }
  },
};

export default orderService;
