export const endpoints = {
  auth: {
    login: '/tai-khoan/dang-nhap',
    register: '/tai-khoan/dang-ky',
  },
  books: {
    list: '/user/books',
    detail: (id: number) => `/user/books/${id}`,
  },
  categories: {
    list: '/api/categories',
  },
} as const;
