import api from '@/services/api/axiosClient';

export interface WishlistItem {
  id?: number;
  bookId: number;
  book?: {
    id: number;
    title: string;
    cover?: string;
    price: number;
  };
  addedAt?: string;
}

const wishlistService = {
  // Lấy danh sách wishlist
  getWishlist: async (page: number = 0, size: number = 50): Promise<any> => {
    try {
      const response: any = await api.get(`/user/wishlist?page=${page}&size=${size}`);
      const pageData = response?.content ? response : response?.data?.content ? response.data : { content: [] };
      return (pageData.content || []).map((item: any) => ({
        id: item.bookId,
        bookId: item.bookId,
        title: item.bookTitle,
        author: { name: item.bookAuthor },
        cover: item.bookCoverImage,
        price: Number(item.bookPrice ?? 0),
        description: item.bookDescription,
        publisher: item.bookPublisher,
        addedAt: item.addedAt,
      }));
    } catch (error: any) {
      throw error;
    }
  },

  // Thêm sách vào wishlist
  addToWishlist: async (bookId: number): Promise<any> => {
    try {
      const response = await api.post(`/user/wishlist/books/${bookId}`);
      return response;
    } catch (error: any) {
      throw error;
    }
  },

  // Xóa sách khỏi wishlist
  removeFromWishlist: async (bookId: number): Promise<any> => {
    try {
      const response = await api.delete(`/user/wishlist/books/${bookId}`);
      return response;
    } catch (error: any) {
      throw error;
    }
  },

  // Kiểm tra sách có trong wishlist không
  isInWishlist: async (bookId: number): Promise<boolean> => {
    try {
      const response: any = await api.get(`/user/wishlist/books/${bookId}/check`);
      return !!response?.isInWishlist;
    } catch (error: any) {
      return false;
    }
  },
};

export default wishlistService;
