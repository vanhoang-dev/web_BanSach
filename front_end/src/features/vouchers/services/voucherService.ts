import api from '@/services/api/axiosClient';
import { unwrapApiData, unwrapPage } from '@/services/api/response';

export interface Voucher {
  id?: number;
  code: string;
  discountPercent: number;
  maxDiscount: number;
  quantity: number;
  usedQuantity?: number;
  expiredAt?: string;
  isExpired?: boolean;
  isValid?: boolean;
}

const normalizeVoucher = (item: any): Voucher => ({
  id: item?.id,
  code: item?.code,
  discountPercent: Number(item?.discountPercent || 0),
  maxDiscount: Number(item?.maxDiscount || 0),
  quantity: Number(item?.quantity || 0),
  usedQuantity: Number(item?.usedQuantity || 0),
  expiredAt: item?.expiredAt,
  isExpired: item?.isExpired,
  isValid: item?.isValid,
});

const voucherService = {
  getVouchers: async (page: number = 0, size: number = 12): Promise<any> => {
    const response = await api.get(`/user/vouchers?page=${page}&size=${size}`);
    const pageData = unwrapPage<any>(response);
    return {
      data: {
        ...pageData,
        content: (pageData.content || []).map(normalizeVoucher),
      },
    };
  },

  getByCode: async (code: string): Promise<Voucher> =>
    normalizeVoucher(unwrapApiData(await api.get(`/user/vouchers/code/${encodeURIComponent(code)}`))),

  getMyVouchers: async (page: number = 0, size: number = 20): Promise<any> => {
    const response = await api.get(`/user/vouchers/my?page=${page}&size=${size}`);
    const pageData = unwrapPage<any>(response);
    return {
      data: {
        ...pageData,
        content: (pageData.content || []).map(normalizeVoucher),
      },
    };
  },

  getClaimedVouchers: async (page: number = 0, size: number = 100): Promise<any> => {
    const response = await api.get(`/user/vouchers/claimed?page=${page}&size=${size}`);
    const pageData = unwrapPage<any>(response);
    return {
      data: {
        ...pageData,
        content: (pageData.content || []).map(normalizeVoucher),
      },
    };
  },

  claimVoucher: async (voucherId: number): Promise<Voucher> =>
    normalizeVoucher(unwrapApiData(await api.post(`/user/vouchers/${voucherId}/claim`, {}))),
};

export default voucherService;
