import api from '@/services/api/axiosClient';
import { unwrapPage } from '@/services/api/response';

export interface AuthorItem {
  id: number;
  authorName: string;
  biography?: string;
}

const normalize = (item: any): AuthorItem => ({
  id: item?.id,
  authorName: item?.authorName || item?.name || '',
  biography: item?.biography || '',
});

const authorService = {
  getAuthors: async (page: number = 0, size: number = 24): Promise<any> => {
    const response = await api.get(`/api/authors?page=${page}&size=${size}`);
    const pageData = unwrapPage<any>(response);
    return { data: { ...pageData, content: (pageData.content || []).map(normalize) } };
  },

  searchAuthors: async (keyword: string, page: number = 0, size: number = 24): Promise<any> => {
    const response = await api.get(`/api/authors/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`);
    const pageData = unwrapPage<any>(response);
    return { data: { ...pageData, content: (pageData.content || []).map(normalize) } };
  },
};

export default authorService;
