import { lazy } from 'react';
import { Route } from 'react-router-dom';

import { MainLayout } from '@/components/layout/Layout';
const ForbiddenPage = lazy(() => import('@/features/errors/pages/ForbiddenPage'));
const NetworkErrorPage = lazy(() => import('@/features/errors/pages/NetworkErrorPage'));
const NotFoundPage = lazy(() => import('@/features/errors/pages/NotFoundPage'));
const ServerErrorPage = lazy(() => import('@/features/errors/pages/ServerErrorPage'));
const UnauthorizedPage = lazy(() => import('@/features/errors/pages/UnauthorizedPage'));

export const errorRoutes = (
  <>
    <Route path="/401" element={<MainLayout><UnauthorizedPage /></MainLayout>} />
    <Route path="/403" element={<MainLayout><ForbiddenPage /></MainLayout>} />
    <Route path="/404" element={<MainLayout><NotFoundPage /></MainLayout>} />
    <Route path="/500" element={<MainLayout><ServerErrorPage /></MainLayout>} />
    <Route path="/network-error" element={<MainLayout><NetworkErrorPage /></MainLayout>} />
  </>
);
