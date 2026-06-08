import { Route } from 'react-router-dom';

import { ProtectedRoute } from '@/app/routes/protectedRoutes';
import { MainLayout } from '@/components/layout/Layout';
import ProfilePage from '@/features/auth/pages/ProfilePage';
import CartPage from '@/features/cart/pages/CartPage';
import CheckoutPage from '@/features/cart/pages/CheckoutPage';
import OrderDetailPage from '@/features/orders/pages/OrderDetailPage';
import OrdersPage from '@/features/orders/pages/OrdersPage';
import WishlistPage from '@/features/wishlist/pages/WishlistPage';

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
