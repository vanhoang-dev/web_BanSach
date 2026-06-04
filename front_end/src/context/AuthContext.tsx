import React, { createContext, useState, useEffect, ReactNode } from 'react';
import authService from '../services/auth';

export interface User {
  id: number;
  email: string;
  fullName: string;
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

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  // Kiểm tra token khi component mount
  useEffect(() => {
    const token = localStorage.getItem('authToken');
    if (token) {
      const storedUser = localStorage.getItem('user');
      if (storedUser) {
        setUser(JSON.parse(storedUser));
      }
    }
    setLoading(false);
  }, []);

  const login = async (email: string, password: string) => {
    try {
      setLoading(true);
      const response = await authService.login({ email, password });
      
      if (response.success && response.data) {
        const userData: User = {
          id: response.data.user?.id || 0,
          email: response.data.user?.email || email,
          fullName: response.data.user?.fullName || '',
          role: response.data.user?.role || 'USER',
        };
        setUser(userData);
        localStorage.setItem('user', JSON.stringify(userData));
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
