import api from '@/services/api/axiosClient';
import { unwrapApiData } from '@/services/api/response';

export interface PaymentRequest {
  orderId: number;
  amount: number;
  returnUrl?: string;
  description?: string;
}

export interface PaymentResponse {
  paymentId: number;
  paymentUrl?: string;
  transactionId?: string;
  amount: number;
  status: 'PENDING' | 'SUCCESS' | 'FAILED' | string;
  message?: string;
}

const paymentService = {
  initiatePayment: async (payload: PaymentRequest): Promise<PaymentResponse> =>
    unwrapApiData<PaymentResponse>(await api.post('/api/payment/initiate', payload)),

  getPaymentStatus: async (paymentId: number): Promise<PaymentResponse> =>
    unwrapApiData<PaymentResponse>(await api.get(`/api/payment/status/${paymentId}`)),

  getOrderPaymentStatus: async (orderId: number): Promise<PaymentResponse> =>
    unwrapApiData<PaymentResponse>(await api.get(`/api/payment/status/order/${orderId}`)),
};

export default paymentService;
