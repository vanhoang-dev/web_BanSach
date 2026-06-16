import api from '@/services/api/axiosClient';
import { unwrapApiData, unwrapPage } from '@/services/api/response';

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

export interface CreateOrderPayload {
  receiverName: string;
  receiverPhone: string;
  shippingAddress: string;
  shippingMethod?: string;
  shippingFee?: number;
  voucherCode?: string;
}

export interface BuyNowOrderPayload extends CreateOrderPayload {
  bookId: number;
  quantity: number;
}

export interface Order {
  id?: number;
  items: OrderItem[];
  totalPrice: number;
  totalAmount?: number;
  shippingAddress: string;
  receiverPhone: string;
  receiverName: string;
  phoneNumber?: string;
  fullName?: string;
  shippingMethod?: string;
  shippingFee?: number;
  voucherCode?: string;
  voucherDiscount?: number;
  status?: 'PENDING' | 'CONFIRMED' | 'SHIPPING' | 'DELIVERED' | 'CANCELLED';
  paymentStatus?: 'UNPAID' | 'PAID';
  orderDate?: string;
  createdAt?: string;
}

const mapOrder = (order: any): Order => ({
  id: order?.id,
  items: order?.items || [],
  totalPrice: Number(order?.totalAmount ?? 0),
  totalAmount: Number(order?.totalAmount ?? 0),
  status: order?.status,
  receiverName: order?.receiverName,
  receiverPhone: order?.receiverPhone,
  fullName: order?.receiverName,
  phoneNumber: order?.receiverPhone,
  shippingAddress: order?.shippingAddress,
  voucherCode: order?.voucherCode,
  voucherDiscount: Number(order?.voucherDiscount ?? 0),
  orderDate: order?.orderDate,
  createdAt: order?.orderDate,
});

const orderService = {
  createOrder: async (orderData: CreateOrderPayload): Promise<Order> => {
    const payload = {
      receiverName: orderData.receiverName,
      receiverPhone: orderData.receiverPhone,
      shippingAddress: orderData.shippingAddress,
      shippingMethod: orderData.shippingMethod || 'STANDARD',
      shippingFee: Number(orderData.shippingFee ?? 0),
      voucherCode: orderData.voucherCode || undefined,
    };
    const response = await api.post('/user/orders', payload);
    return mapOrder(unwrapApiData(response));
  },

  buyNow: async (orderData: BuyNowOrderPayload): Promise<Order> => {
    const payload = {
      receiverName: orderData.receiverName,
      receiverPhone: orderData.receiverPhone,
      shippingAddress: orderData.shippingAddress,
      shippingMethod: orderData.shippingMethod || 'STANDARD',
      shippingFee: Number(orderData.shippingFee ?? 0),
      voucherCode: orderData.voucherCode || undefined,
      bookId: Number(orderData.bookId),
      quantity: Number(orderData.quantity),
    };
    const response = await api.post('/user/orders/buy-now', payload);
    return mapOrder(unwrapApiData(response));
  },

  getOrders: async (page: number = 0, size: number = 10): Promise<any> => {
    const response: any = await api.get(`/user/orders?page=${page}&size=${size}`);
    const pageData = unwrapPage<any>(response);
    return {
      data: {
        ...pageData,
        content: pageData.content.map(mapOrder),
      },
    };
  },

  getOrderById: async (id: number): Promise<Order> => {
    const response: any = await api.get(`/user/orders/${id}`);
    return mapOrder(unwrapApiData(response));
  },

  cancelOrder: async (id: number): Promise<any> => api.put(`/user/orders/${id}/cancel`, {}),

  checkPaymentStatus: async (orderId: number): Promise<any> => api.get(`/api/payment/status/order/${orderId}`),
};

export default orderService;
