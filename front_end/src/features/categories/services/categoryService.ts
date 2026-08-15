import api from '@/services/api/axiosClient';
import { unwrapPage } from '@/services/api/response';

export interface CategoryItem {
  id: number;
  name: string;
  description?: string;
  isActive?: boolean;
}

export interface CategoryPage {
  content: CategoryItem[];
  totalElements: number;
  totalPages: number;
}

const normalize = (item: any): CategoryItem => ({
  id: item?.id,
  name: item?.name,
  description: item?.description,
  isActive: item?.isActive,
});

const categoryService = {
  getCategories: async (page: number = 0, size: number = 24): Promise<CategoryPage> => {
    const response = await api.get(`/api/categories?page=${page}&size=${size}`);
    const pageData = unwrapPage<any>(response);
    return { ...pageData, content: (pageData.content || []).map(normalize) };
  },

  searchCategories: async (keyword: string, page: number = 0, size: number = 24): Promise<CategoryPage> => {
    const response = await api.get(`/api/categories/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`);
    const pageData = unwrapPage<any>(response);
    return { ...pageData, content: (pageData.content || []).map(normalize) };
  },
};

export default categoryService;
