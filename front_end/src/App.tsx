import './App.css';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { MainLayout, AdminLayout, BlankLayout } from './components/layout/Layout';
import { AuthProvider } from './context/AuthContext';
import { ProtectedRoute } from './utils/ProtectedRoute';

// User Pages
import HomePage from './pages/HomePage';
import CatalogPage from './pages/CatalogPage';
import CategoriesPage from './pages/CategoriesPage';
import AuthorsPage from './pages/AuthorsPage';
import PromotionsPage from './pages/PromotionsPage';
import SearchResultsPage from './pages/SearchResultsPage';
import BookDetailPage from './pages/BookDetailPage';
import CartPage from './pages/CartPage';
import CheckoutPage from './pages/CheckoutPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ForgotPasswordPage from './pages/ForgotPasswordPage';
import ProfilePage from './pages/ProfilePage';
import WishlistPage from './pages/WishlistPage';
import OrdersPage from './pages/OrdersPage';
import OrderDetailPage from './pages/OrderDetailPage';

// Admin Pages
import AdminDashboard from './pages/AdminDashboard';
import AdminCategoryManagementPage from './pages/AdminCategoryManagementPage';
import AdminOrderManagementPage from './pages/AdminOrderManagementPage';
import AdminBookManagementPage from './pages/AdminBookManagementPage';
import AdminUserManagementPage from './pages/AdminUserManagementPage';
import AdminInventoryPage from './pages/AdminInventoryPage';
import AdminVoucherManagementPage from './pages/AdminVoucherManagementPage';
import AdminAuthorManagementPage from './pages/AdminAuthorManagementPage';
import AdminRefundPage from './pages/AdminRefundPage';

/**
 * Main App Component with React Router
 */
function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* User Routes */}
          <Route path="/" element={<MainLayout><HomePage /></MainLayout>} />
          <Route path="/new-books" element={<MainLayout><CatalogPage /></MainLayout>} />
          <Route path="/catalog" element={<MainLayout><CatalogPage /></MainLayout>} />
          <Route path="/categories" element={<MainLayout><CategoriesPage /></MainLayout>} />
          <Route path="/authors" element={<MainLayout><AuthorsPage /></MainLayout>} />
          <Route path="/promotions" element={<MainLayout><PromotionsPage /></MainLayout>} />
          <Route path="/search" element={<MainLayout><SearchResultsPage /></MainLayout>} />
          <Route path="/books/:id" element={<MainLayout><BookDetailPage /></MainLayout>} />
          <Route path="/cart" element={<MainLayout><CartPage /></MainLayout>} />
          <Route path="/checkout" element={<ProtectedRoute><MainLayout><CheckoutPage /></MainLayout></ProtectedRoute>} />
          <Route path="/profile" element={<ProtectedRoute><MainLayout><ProfilePage /></MainLayout></ProtectedRoute>} />
          <Route path="/wishlist" element={<ProtectedRoute><MainLayout><WishlistPage /></MainLayout></ProtectedRoute>} />
          <Route path="/orders" element={<ProtectedRoute><MainLayout><OrdersPage /></MainLayout></ProtectedRoute>} />
          <Route path="/orders/:id" element={<ProtectedRoute><MainLayout><OrderDetailPage /></MainLayout></ProtectedRoute>} />

          {/* Auth Routes (BlankLayout) */}
          <Route path="/login" element={<BlankLayout><LoginPage /></BlankLayout>} />
          <Route path="/register" element={<BlankLayout><RegisterPage /></BlankLayout>} />
          <Route path="/forgot-password" element={<BlankLayout><ForgotPasswordPage /></BlankLayout>} />

          {/* Admin Routes */}
          <Route path="/admin" element={<Navigate to="/admin/dashboard" replace />} />
          <Route path="/admin/dashboard" element={<ProtectedRoute adminOnly><AdminLayout><AdminDashboard /></AdminLayout></ProtectedRoute>} />
          <Route path="/admin/books" element={<ProtectedRoute adminOnly><AdminLayout><AdminBookManagementPage /></AdminLayout></ProtectedRoute>} />
          <Route path="/admin/categories" element={<ProtectedRoute adminOnly><AdminLayout><AdminCategoryManagementPage /></AdminLayout></ProtectedRoute>} />
          <Route path="/admin/orders" element={<ProtectedRoute adminOnly><AdminLayout><AdminOrderManagementPage /></AdminLayout></ProtectedRoute>} />
          <Route path="/admin/users" element={<ProtectedRoute adminOnly><AdminLayout><AdminUserManagementPage /></AdminLayout></ProtectedRoute>} />
          <Route path="/admin/inventory" element={<ProtectedRoute adminOnly><AdminLayout><AdminInventoryPage /></AdminLayout></ProtectedRoute>} />
          <Route path="/admin/vouchers" element={<ProtectedRoute adminOnly><AdminLayout><AdminVoucherManagementPage /></AdminLayout></ProtectedRoute>} />
          <Route path="/admin/authors" element={<ProtectedRoute adminOnly><AdminLayout><AdminAuthorManagementPage /></AdminLayout></ProtectedRoute>} />
          <Route path="/admin/refunds" element={<ProtectedRoute adminOnly><AdminLayout><AdminRefundPage /></AdminLayout></ProtectedRoute>} />

          {/* Fallback Route */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
