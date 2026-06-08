import api from '@/services/api/axiosClient';
import { tokenStorage } from '@/services/storage/tokenStorage';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  fullName: string;
  email: string;
  password: string;
  confirmPassword?: string;
}

export interface AuthResponse {
  success: boolean;
  message: string;
  data?: {
    token: string;
    user?: {
      id: number;
      email: string;
      fullName: string;
      role: 'USER' | 'ADMIN';
    };
  };
}

interface LoginBackendResponse {
  jwt?: string;
  roles?: string[];
  userId?: number;
  username?: string;
}

const authService = {
  // Đăng nhập
  login: async (credentials: LoginRequest): Promise<any> => {
    try {
      const response = await api.post<LoginBackendResponse, LoginBackendResponse>(
        '/tai-khoan/dang-nhap',
        credentials
      );

      const token = response?.jwt;
      const roles: string[] = response?.roles || [];
      const user = {
        id: response?.userId || 0,
        email: response?.username || credentials.email,
        fullName: '',
        role: roles.includes('ADMIN') ? 'ADMIN' : 'USER',
      };

      if (token) {
        tokenStorage.setToken(token);
        tokenStorage.setUser(user);
      }

      return {
        success: !!token,
        message: token ? 'Đăng nhập thành công' : 'Đăng nhập thất bại',
        data: {
          token,
          user,
        },
      };
    } catch (error: any) {
      throw error;
    }
  },

  // Đăng ký
  register: async (data: RegisterRequest): Promise<any> => {
    try {
      const usernameFromEmail = (data.email || '').split('@')[0] || 'user';
      const response = await api.post('/tai-khoan/dang-ky', {
        username: usernameFromEmail,
        password: data.password,
        fullName: data.fullName,
        email: data.email,
        phone: '',
        address: '',
      });
      return response;
    } catch (error: any) {
      throw error;
    }
  },

  // Đăng xuất
  logout: () => {
    tokenStorage.clear();
  },

  // Lấy token từ localStorage
  getToken: () => {
    return tokenStorage.getToken();
  },

  // Lấy user info từ localStorage
  getUser: () => {
    return tokenStorage.getUser();
  },

  // Kiểm tra user đã đăng nhập
  isAuthenticated: () => {
    return !!tokenStorage.getToken();
  },

  // Kiểm tra user là admin
  isAdmin: () => {
    const user = authService.getUser();
    return user?.role === 'ADMIN';
  },
};

export default authService;
