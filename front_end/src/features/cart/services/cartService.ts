import api from '@/services/api/axiosClient';
import { unwrapApiData } from '@/services/api/response';

export interface CartItem {
  id?: number;
  bookId: number;
  quantity: number;
  price?: number;
  subtotal?: number;
  book?: {
    id: number;
    title: string;
    cover?: string;
    price: number;
  };
}

export interface Cart {
  id?: number;
  items: CartItem[];
  totalPrice?: number;
  totalQuantity?: number;
}

const normalizeCart = (response: any): Cart => {
  const raw = unwrapApiData<any>(response) || {};
  const items = (raw.items || []).map((item: any) => ({
    id: item.id,
    bookId: item.bookId,
    quantity: Number(item.quantity ?? 0),
    price: Number(item.priceAfterDiscount ?? item.bookPrice ?? 0),
    subtotal: Number(item.subtotal ?? 0),
    book: {
      id: item.bookId,
      title: item.bookTitle,
      cover: item.bookCoverImage,
      price: Number(item.bookPrice ?? 0),
    },
  }));

  return {
    id: raw.cartId,
    items,
    totalPrice: Number(raw.totalAmount ?? 0),
    totalQuantity: Number(raw.totalItems ?? 0),
  };
};

const findCartItemIdByBookId = async (bookId: number): Promise<number> => {
  const cartRaw: any = await api.get('/user/cart');
  const items = unwrapApiData<any>(cartRaw)?.items || [];
  const target = items.find((item: any) => Number(item.bookId) === Number(bookId));
  if (!target?.id) {
    throw new Error('Không tìm thấy sản phẩm trong giỏ hàng');
  }
  return target.id;
};

const cartService = {
  getCart: async (): Promise<Cart> => {
    const response = await api.get('/user/cart');
    return normalizeCart(response);
  },

  addToCart: async (bookId: number, quantity: number = 1): Promise<Cart> => {
    const response = await api.post('/user/cart/items', { bookId, quantity });
    return normalizeCart(response);
  },

  updateCartItem: async (bookId: number, quantity: number): Promise<Cart> => {
    const itemId = await findCartItemIdByBookId(bookId);
    const response = await api.put(`/user/cart/items/${itemId}`, { quantity });
    return normalizeCart(response);
  },

  removeFromCart: async (bookId: number): Promise<Cart> => {
    const itemId = await findCartItemIdByBookId(bookId);
    const response = await api.delete(`/user/cart/items/${itemId}`);
    return normalizeCart(response);
  },

  clearCart: async (): Promise<any> => api.delete('/user/cart/clear'),
};

export default cartService;
