import api from '@/services/api/axiosClient';

export interface Book {
  id?: number;
  title: string;
  description?: string;
  author?: {
    id: number;
    name: string;
  };
  category?: {
    id: number;
    name: string;
  };
  price: number;
  discount?: number;
  cover?: string;
  rating?: number;
  reviews?: number;
  stock?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface Category {
  id: number;
  name: string;
  description?: string;
  bookCount?: number;
}

const mapBookFromBackend = (book: any): Book => ({
  id: book?.id,
  title: book?.title,
  description: book?.description,
  author: book?.authorName ? { id: 0, name: book.authorName } : undefined,
  category: book?.categoryName ? { id: 0, name: book.categoryName } : undefined,
  price: Number(book?.price || 0),
  discount: book?.discountPercent,
  cover: book?.coverImage,
});

const unwrapPage = (response: any) => {
  if (response?.data?.content) {
    return response.data;
  }
  if (response?.content) {
    return response;
  }
  return { content: [], totalPages: 0, totalElements: 0, number: 0, size: 10 };
};

const bookService = {
  // Lấy danh sách sách với pagination và filter
  getBooks: async (page: number = 0, size: number = 12, search?: string, categoryId?: number): Promise<any> => {
    try {
      let url = `/user/books?page=${page}&size=${size}`;
      if (search) url += `&keyword=${encodeURIComponent(search)}`;
      if (categoryId) url += `&categoryId=${categoryId}`;
      const response: any = await api.get(url);
      const pageData = unwrapPage(response);
      return {
        data: {
          ...pageData,
          content: (pageData.content || []).map(mapBookFromBackend),
        },
      };
    } catch (error: any) {
      throw error;
    }
  },

  // Lấy chi tiết sách theo ID
  getBookById: async (id: number): Promise<Book> => {
    try {
      const response: any = await api.get(`/user/books/${id}`);
      const raw = response?.data || response;
      return mapBookFromBackend(raw);
    } catch (error: any) {
      throw error;
    }
  },

  // Lấy danh sách danh mục
  getCategories: async (): Promise<Category[]> => {
    try {
      const response: any = await api.get('/api/categories');
      const pageData = response?.data?.content ? response.data : response?.content ? response : null;
      const items = pageData?.content || [];
      return items.map((item: any) => ({
        id: item.id,
        name: item.name,
        description: item.description,
      }));
    } catch (error: any) {
      throw error;
    }
  },

  // Tìm kiếm sách
  searchBooks: async (keyword: string, page: number = 0, size: number = 12): Promise<any> => {
    try {
      const response: any = await api.get(
        `/user/books?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`
      );
      const pageData = unwrapPage(response);
      return {
        data: {
          ...pageData,
          content: (pageData.content || []).map(mapBookFromBackend),
        },
      };
    } catch (error: any) {
      throw error;
    }
  },

  // Lấy sách theo danh mục
  getBooksByCategory: async (categoryId: number, page: number = 0, size: number = 12): Promise<any> => {
    try {
      const response: any = await api.get(
        `/user/books?categoryId=${categoryId}&page=${page}&size=${size}`
      );
      const pageData = unwrapPage(response);
      return {
        data: {
          ...pageData,
          content: (pageData.content || []).map(mapBookFromBackend),
        },
      };
    } catch (error: any) {
      throw error;
    }
  },

  // Lấy sách nổi bật/best seller
  getFeaturedBooks: async (limit: number = 8): Promise<Book[]> => {
    try {
      const response: any = await api.get(`/user/books?page=0&size=${limit}`);
      const pageData = unwrapPage(response);
      return (pageData.content || []).map(mapBookFromBackend);
    } catch (error: any) {
      throw error;
    }
  },
};

export default bookService;
