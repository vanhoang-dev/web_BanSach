import { Route } from 'react-router-dom';

import { BlankLayout, MainLayout } from '@/components/layout/Layout';
import ForgotPasswordPage from '@/features/auth/pages/ForgotPasswordPage';
import LoginPage from '@/features/auth/pages/LoginPage';
import RegisterPage from '@/features/auth/pages/RegisterPage';
import AuthorsPage from '@/features/authors/pages/AuthorsPage';
import BookDetailPage from '@/features/books/pages/BookDetailPage';
import CatalogPage from '@/features/books/pages/CatalogPage';
import HomePage from '@/features/books/pages/HomePage';
import PromotionsPage from '@/features/books/pages/PromotionsPage';
import SearchResultsPage from '@/features/books/pages/SearchResultsPage';
import CategoriesPage from '@/features/categories/pages/CategoriesPage';

export const publicRoutes = (
  <>
    <Route path="/" element={<MainLayout><HomePage /></MainLayout>} />
    <Route path="/new-books" element={<MainLayout><CatalogPage /></MainLayout>} />
    <Route path="/catalog" element={<MainLayout><CatalogPage /></MainLayout>} />
    <Route path="/categories" element={<MainLayout><CategoriesPage /></MainLayout>} />
    <Route path="/authors" element={<MainLayout><AuthorsPage /></MainLayout>} />
    <Route path="/promotions" element={<MainLayout><PromotionsPage /></MainLayout>} />
    <Route path="/search" element={<MainLayout><SearchResultsPage /></MainLayout>} />
    <Route path="/books/:id" element={<MainLayout><BookDetailPage /></MainLayout>} />
    <Route path="/login" element={<BlankLayout><LoginPage /></BlankLayout>} />
    <Route path="/register" element={<BlankLayout><RegisterPage /></BlankLayout>} />
    <Route path="/forgot-password" element={<BlankLayout><ForgotPasswordPage /></BlankLayout>} />
  </>
);
