import React, { createContext, useEffect, useState, ReactNode } from 'react';

import authService from '@/features/auth/services';
import { tokenStorage } from '@/services/storage/tokenStorage';

export interface User {
  id: number;
  email: string;
  fullName: string;
  phone?: string;
  address?: string;
  role: 'USER' | 'ADMIN';
}

export interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  isAdmin: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (fullName: string, email: string, password: string) => Promise<void>;
  logout: () => void;
  loading: boolean;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

const normalizeUser = (baseUser: Partial<User>, profile?: any): User => ({
  id: profile?.id || profile?.userId || baseUser.id || 0,
  email: profile?.email || baseUser.email || '',
  fullName: profile?.fullName || profile?.full_name || '',
  phone: profile?.phone || baseUser.phone || '',
  address: profile?.address || baseUser.address || '',
  role: profile?.role || baseUser.role || 'USER',
});

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadCurrentUser = async () => {
      const token = tokenStorage.getToken();
      if (!token) {
        setLoading(false);
        return;
      }

      const storedUser = tokenStorage.getUser();
      if (storedUser) setUser(storedUser);

      try {
        const profile = await authService.getProfile();
        const userData = normalizeUser(storedUser || {}, profile);
        setUser(userData);
        tokenStorage.setUser(userData);
      } catch {
        if (!storedUser) tokenStorage.clear();
      } finally {
        setLoading(false);
      }
    };

    loadCurrentUser();
  }, []);

  const login = async (email: string, password: string) => {
    try {
      setLoading(true);
      const response = await authService.login({ email, password });

      if (response.success && response.data) {
        const loginUser = normalizeUser({
          id: response.data.user?.id || 0,
          email: response.data.user?.email || email,
          role: response.data.user?.role || 'USER',
        });
        const profile = await authService.getProfile();
        const userData = normalizeUser(loginUser, profile);
        setUser(userData);
        tokenStorage.setUser(userData);
      }
      setLoading(false);
    } catch (error: any) {
      setLoading(false);
      throw error;
    }
  };

  const register = async (fullName: string, email: string, password: string): Promise<void> => {
    try {
      setLoading(true);
      await authService.register({ fullName, email, password });
      setLoading(false);
    } catch (error: any) {
      setLoading(false);
      throw error;
    }
  };

  const logout = () => {
    setUser(null);
    authService.logout();
  };

  const value: AuthContextType = {
    user,
    isAuthenticated: !!user,
    isAdmin: user?.role === 'ADMIN',
    login,
    register,
    logout,
    loading,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
