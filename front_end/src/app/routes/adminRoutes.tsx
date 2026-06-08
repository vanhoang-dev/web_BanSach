import type { ReactNode } from 'react';
import { Navigate, Route } from 'react-router-dom';

import { ProtectedRoute } from '@/app/routes/protectedRoutes';
import { AdminLayout } from '@/components/layout/Layout';
import AdminAuthorManagementPage from '@/features/admin/pages/AdminAuthorManagementPage';
import AdminBookManagementPage from '@/features/admin/pages/AdminBookManagementPage';
import AdminCategoryManagementPage from '@/features/admin/pages/AdminCategoryManagementPage';
import AdminDashboard from '@/features/admin/pages/AdminDashboard';
import AdminInventoryPage from '@/features/admin/pages/AdminInventoryPage';
import AdminOrderManagementPage from '@/features/admin/pages/AdminOrderManagementPage';
import AdminUserManagementPage from '@/features/admin/pages/AdminUserManagementPage';
import AdminVoucherManagementPage from '@/features/admin/pages/AdminVoucherManagementPage';

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
