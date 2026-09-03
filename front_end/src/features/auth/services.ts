import api from '@/services/api/axiosClient';
import { unwrapApiData } from '@/services/api/response';
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
  phone?: string;
  address?: string;
}

export interface UpdateProfileRequest {
  fullName?: string;
  phone?: string;
  address?: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
  confirmPassword?: string;
}

interface LoginBackendResponse {
  jwt?: string;
  roles?: string[];
  userId?: number;
  username?: string;
}

const toFrontendRole = (roles: string[] = []): 'USER' | 'ADMIN' =>
  roles.includes('ROLE_ADMIN') || roles.includes('ADMIN') ? 'ADMIN' : 'USER';

const authService = {
  login: async (credentials: LoginRequest): Promise<any> => {
    const response = await api.post('/tai-khoan/dang-nhap', credentials);
    const payload = unwrapApiData<LoginBackendResponse>(response);

    const token = payload?.jwt;
    const roles = payload?.roles || [];
    const user = {
      id: payload?.userId || 0,
      email: payload?.username || credentials.email,
      fullName: '',
      role: toFrontendRole(roles),
    };

    if (token) {
      tokenStorage.setToken(token);
      tokenStorage.setUser(user);
    }

    return {
      success: !!token,
      message: token ? 'Đăng nhập thành công' : 'Đăng nhập thất bại',
      data: { token, user },
    };
  },

  register: async (data: RegisterRequest): Promise<any> => {
    const usernameFromEmail = (data.email || '').split('@')[0] || 'user';
    return api.post('/tai-khoan/dang-ky', {
      username: usernameFromEmail,
      password: data.password,
      fullName: data.fullName,
      email: data.email,
      phone: data.phone || '',
      address: data.address || '',
    });
  },

  getProfile: async (): Promise<any> => unwrapApiData(await api.get('/user/me')),

  updateProfile: async (data: UpdateProfileRequest): Promise<any> =>
    unwrapApiData(await api.put('/user/update-profile', data)),

  changePassword: async (data: ChangePasswordRequest): Promise<any> =>
    unwrapApiData(await api.post('/user/change-password', data)),

  forgotPassword: async (data: ForgotPasswordRequest): Promise<any> =>
    unwrapApiData(await api.post('/tai-khoan/quen-mat-khau', data)),

  resetPassword: async (data: ResetPasswordRequest): Promise<any> =>
    unwrapApiData(await api.post('/tai-khoan/dat-lai-mat-khau', {
      resetToken: data.token,
      newPassword: data.newPassword,
      confirmPassword: data.confirmPassword || data.newPassword,
    })),

  logout: () => {
    tokenStorage.clear();
  },

  getToken: () => tokenStorage.getToken(),

  getUser: () => tokenStorage.getUser(),

  isAuthenticated: () => !!tokenStorage.getToken(),

  isAdmin: () => {
    const user = tokenStorage.getUser();
    return user?.role === 'ADMIN';
  },
};

export default authService;
