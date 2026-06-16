import api from '@/services/api/axiosClient';
import { unwrapApiData, unwrapPage } from '@/services/api/response';

export interface Review {
  id?: number;
  bookId: number;
  rating: number;
  comment: string;
  userId?: number;
  userName?: string;
  createdAt?: string;
}

export interface ReviewResponse {
  success: boolean;
  data?: Review;
  message?: string;
}

const reviewService = {
  // Lấy danh sách review của sách
  getReviewsByBook: async (bookId: number, page: number = 0, size: number = 5): Promise<any> => {
    try {
      const response: any = await api.get(`/user/reviews/book/${bookId}?page=${page}&size=${size}`);
      const pageData = unwrapPage<any>(response);
      return {
        data: {
          ...pageData,
          content: (pageData.content || []).map((item: any) => ({
            id: item.id,
            bookId: item.bookId,
            rating: item.rating,
            comment: item.comment,
            userId: item.userId,
            userName: item.userName,
            createdAt: item.createdAt,
          })),
        },
      };
    } catch (error: any) {
      throw error;
    }
  },

  getReviewStats: async (bookId: number): Promise<{ averageRating: number; reviewCount: number }> =>
    unwrapApiData(await api.get(`/user/reviews/book/${bookId}/stats`)),

  getMyReview: async (bookId: number): Promise<Review | null> => {
    try {
      return unwrapApiData<Review>(await api.get(`/user/reviews/book/${bookId}/my-review`));
    } catch {
      return null;
    }
  },

  // Thêm review cho sách
  addReview: async (review: Review): Promise<any> => {
    try {
      const response = await api.post('/user/reviews', {
        bookId: review.bookId,
        rating: review.rating,
        comment: review.comment,
      });
      return unwrapApiData(response);
    } catch (error: any) {
      throw error;
    }
  },

  // Cập nhật review
  updateReview: async (id: number, review: Review): Promise<any> => {
    try {
      const response = await api.put(`/user/reviews/${id}`, {
        bookId: review.bookId,
        rating: review.rating,
        comment: review.comment,
      });
      return unwrapApiData(response);
    } catch (error: any) {
      throw error;
    }
  },

  // Xóa review
  deleteReview: async (id: number): Promise<any> => {
    try {
      const response = await api.delete(`/user/reviews/${id}`);
      return response;
    } catch (error: any) {
      throw error;
    }
  },

  getUserReviewsForAdmin: async (userId: number, page: number = 0, size: number = 10): Promise<any> => {
    const response = await api.get(`/admin/reviews/user/${userId}?page=${page}&size=${size}`);
    const pageData = unwrapPage<any>(response);
    return { data: pageData };
  },

  deleteReviewForAdmin: async (id: number): Promise<any> => api.delete(`/admin/reviews/${id}`),
};

export default reviewService;
