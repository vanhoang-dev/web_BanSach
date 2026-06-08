import api from '@/services/api/axiosClient';

export interface CartItem {
  id?: number;
  bookId: number;
  quantity: number;
  price?: number;
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
  const raw = response?.data || response || {};
  const items = (raw.items || []).map((item: any) => ({
    id: item.id,
    bookId: item.bookId,
    quantity: item.quantity,
    price: Number(item.priceAfterDiscount ?? item.bookPrice ?? 0),
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
  const items = cartRaw?.items || cartRaw?.data?.items || [];
  const target = items.find((item: any) => item.bookId === bookId);
  if (!target?.id) {
    throw new Error('Không tìm thấy sản phẩm trong giỏ hàng');
  }
  return target.id;
};

const cartService = {
  // Lấy giỏ hàng
  getCart: async (): Promise<any> => {
    try {
      const response = await api.get('/user/cart');
      return normalizeCart(response);
    } catch (error: any) {
      throw error;
    }
  },

  // Thêm sách vào giỏ hàng
  addToCart: async (bookId: number, quantity: number = 1): Promise<any> => {
    try {
      const response = await api.post('/user/cart/items', { bookId, quantity });
      return normalizeCart(response);
    } catch (error: any) {
      throw error;
    }
  },

  // Cập nhật số lượng sách trong giỏ hàng
  updateCartItem: async (bookId: number, quantity: number): Promise<any> => {
    try {
      const itemId = await findCartItemIdByBookId(bookId);
      const response = await api.put(`/user/cart/items/${itemId}`, { quantity });
      return normalizeCart(response);
    } catch (error: any) {
      throw error;
    }
  },

  // Xóa sách khỏi giỏ hàng
  removeFromCart: async (bookId: number): Promise<any> => {
    try {
      const itemId = await findCartItemIdByBookId(bookId);
      const response = await api.delete(`/user/cart/items/${itemId}`);
      return normalizeCart(response);
    } catch (error: any) {
      throw error;
    }
  },

  // Xóa toàn bộ giỏ hàng
  clearCart: async (): Promise<any> => {
    try {
      const response = await api.delete('/user/cart/clear');
      return response;
    } catch (error: any) {
      throw error;
    }
  },
};

export default cartService;
