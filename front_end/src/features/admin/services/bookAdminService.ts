import api from '@/services/api/axiosClient';
import { unwrapApiData } from '@/services/api/response';
import { withPage } from '@/features/admin/services/pageResponse';

const toFormData = (bookData: any) => {
  if (bookData instanceof FormData) return bookData;

  const formData = new FormData();
  const file = bookData.coverImageFile || bookData.file || bookData.coverImage;
  const payload = {
    title: bookData.title,
    isbn: bookData.isbn,
    publisher: bookData.publisher,
    publicationYear: Number(bookData.publicationYear),
    price: Number(bookData.price),
    description: bookData.description || '',
    authorId: Number(bookData.authorId),
    categoryId: Number(bookData.categoryId),
    discountId: bookData.discountId ? Number(bookData.discountId) : null,
  };

  Object.entries(payload).forEach(([key, value]) => {
    if (value !== null && value !== undefined) formData.append(key, String(value));
  });
  if (file instanceof File) formData.append('image', file);
  return formData;
};

const multipartConfig = { headers: { 'Content-Type': 'multipart/form-data' } };

const bookAdminService = {
  getAll: async (page = 0, size = 10) => withPage(await api.get(`/admin/books?page=${page}&size=${size}`)),
  getById: async (id: number) => unwrapApiData(await api.get(`/admin/books/${id}`)),
  create: async (bookData: any) =>
    unwrapApiData(await api.post('/admin/books/create-book', toFormData(bookData), multipartConfig)),
  update: async (id: number, bookData: any) =>
    unwrapApiData(await api.put(`/admin/books/update-book/${id}`, toFormData(bookData), multipartConfig)),
  remove: async (id: number) => api.delete(`/admin/books/delete-book/${id}`),
};

export default bookAdminService;
