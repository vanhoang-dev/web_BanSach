import { lazy, type ReactNode } from 'react';
import { Navigate, Route } from 'react-router-dom';

import { ProtectedRoute } from '@/app/routes/protectedRoutes';
import AdminLayout from '@/features/admin/components/AdminLayout';
const AdminAuthorManagementPage = lazy(() => import('@/features/admin/pages/AdminAuthorManagementPage'));
const AdminBookManagementPage = lazy(() => import('@/features/admin/pages/AdminBookManagementPage'));
const AdminCategoryManagementPage = lazy(() => import('@/features/admin/pages/AdminCategoryManagementPage'));
const AdminDashboard = lazy(() => import('@/features/admin/pages/AdminDashboard'));
const AdminInventoryPage = lazy(() => import('@/features/admin/pages/AdminInventoryPage'));
const AdminOrderManagementPage = lazy(() => import('@/features/admin/pages/AdminOrderManagementPage'));
const AdminUserManagementPage = lazy(() => import('@/features/admin/pages/AdminUserManagementPage'));
const AdminVoucherManagementPage = lazy(() => import('@/features/admin/pages/AdminVoucherManagementPage'));

const adminRoute = (page: ReactNode) => (
  <ProtectedRoute adminOnly>
    <AdminLayout>{page}</AdminLayout>
  </ProtectedRoute>
);

export const adminRoutes = (
  <>
    <Route path="/admin" element={<Navigate to="/admin/dashboard" replace />} />
    <Route path="/admin/dashboard" element={adminRoute(<AdminDashboard />)} />
    <Route path="/admin/books" element={adminRoute(<AdminBookManagementPage />)} />
    <Route path="/admin/categories" element={adminRoute(<AdminCategoryManagementPage />)} />
    <Route path="/admin/orders" element={adminRoute(<AdminOrderManagementPage />)} />
    <Route path="/admin/users" element={adminRoute(<AdminUserManagementPage />)} />
    <Route path="/admin/inventory" element={adminRoute(<AdminInventoryPage />)} />
    <Route path="/admin/vouchers" element={adminRoute(<AdminVoucherManagementPage />)} />
    <Route path="/admin/authors" element={adminRoute(<AdminAuthorManagementPage />)} />
  </>
);
