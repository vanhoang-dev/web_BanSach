import { lazy } from 'react';
import { Route } from 'react-router-dom';

import { ProtectedRoute } from '@/app/routes/protectedRoutes';
import { MainLayout } from '@/components/layout/Layout';
const ProfilePage = lazy(() => import('@/features/auth/pages/ProfilePage'));
const CartPage = lazy(() => import('@/features/cart/pages/CartPage'));
const CheckoutPage = lazy(() => import('@/features/cart/pages/CheckoutPage'));
const OrderDetailPage = lazy(() => import('@/features/orders/pages/OrderDetailPage'));
const OrdersPage = lazy(() => import('@/features/orders/pages/OrdersPage'));
const WishlistPage = lazy(() => import('@/features/wishlist/pages/WishlistPage'));

export const userRoutes = (
  <>
    <Route path="/cart" element={<MainLayout><CartPage /></MainLayout>} />
    <Route path="/checkout" element={<ProtectedRoute><MainLayout><CheckoutPage /></MainLayout></ProtectedRoute>} />
    <Route path="/profile" element={<ProtectedRoute><MainLayout><ProfilePage /></MainLayout></ProtectedRoute>} />
    <Route path="/wishlist" element={<ProtectedRoute><MainLayout><WishlistPage /></MainLayout></ProtectedRoute>} />
    <Route path="/orders" element={<ProtectedRoute><MainLayout><OrdersPage /></MainLayout></ProtectedRoute>} />
    <Route path="/orders/:id" element={<ProtectedRoute><MainLayout><OrderDetailPage /></MainLayout></ProtectedRoute>} />
  </>
);
