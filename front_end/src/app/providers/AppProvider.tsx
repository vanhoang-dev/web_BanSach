import type { ReactNode } from 'react';

import { AuthProvider } from '@/features/auth/context/AuthContext';
import { NotificationProvider } from '@/features/notifications/NotificationProvider';

interface AppProviderProps {
  children: ReactNode;
}

export function AppProvider({ children }: AppProviderProps) {
  return (
    <AuthProvider>
      {children}
      <NotificationProvider />
    </AuthProvider>
  );
}
