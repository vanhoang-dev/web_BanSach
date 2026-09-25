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

type CartItemResponse = {
  id?: number;
  bookId?: number;
  quantity?: number;
  priceAfterDiscount?: number;
  bookPrice?: number;
  subtotal?: number;
  bookTitle?: string;
  bookCoverImage?: string;
};

type CartResponse = {
  cartId?: number;
  items?: CartItemResponse[];
  totalAmount?: number;
  totalItems?: number;
};

export const cartChangedEvent = 'cart:changed';

const publishCartChange = (cart: Cart) => {
  window.dispatchEvent(new CustomEvent<Cart>(cartChangedEvent, { detail: cart }));
  return cart;
};

const normalizeCart = (response: unknown): Cart => {
  const raw = unwrapApiData<CartResponse>(response) || {};
  const items = (raw.items || []).map((item) => ({
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
  const cartRaw: unknown = await api.get('/user/cart');
  const items = unwrapApiData<CartResponse>(cartRaw)?.items || [];
  const target = items.find((item) => Number(item.bookId) === Number(bookId));
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
    return publishCartChange(normalizeCart(response));
  },

  updateCartItem: async (bookId: number, quantity: number): Promise<Cart> => {
    const itemId = await findCartItemIdByBookId(bookId);
    const response = await api.put(`/user/cart/items/${itemId}`, { quantity });
    return publishCartChange(normalizeCart(response));
  },

  removeFromCart: async (bookId: number): Promise<Cart> => {
    const itemId = await findCartItemIdByBookId(bookId);
    const response = await api.delete(`/user/cart/items/${itemId}`);
    return publishCartChange(normalizeCart(response));
  },

  clearCart: async (): Promise<void> => {
    await api.delete('/user/cart/clear');
    publishCartChange({ items: [], totalPrice: 0, totalQuantity: 0 });
  },
};

export default cartService;
