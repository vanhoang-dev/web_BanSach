import { lazy } from 'react';
import { Route } from 'react-router-dom';

import { BlankLayout, MainLayout } from '@/components/layout/Layout';
const ForgotPasswordPage = lazy(() => import('@/features/auth/pages/ForgotPasswordPage'));
const LoginPage = lazy(() => import('@/features/auth/pages/LoginPage'));
const RegisterPage = lazy(() => import('@/features/auth/pages/RegisterPage'));
const ResetPasswordPage = lazy(() => import('@/features/auth/pages/ResetPasswordPage'));
const AuthorsPage = lazy(() => import('@/features/authors/pages/AuthorsPage'));
const BookDetailPage = lazy(() => import('@/features/books/pages/BookDetailPage'));
const CatalogPage = lazy(() => import('@/features/books/pages/CatalogPage'));
const HomePage = lazy(() => import('@/features/books/pages/HomePage'));
const PromotionsPage = lazy(() => import('@/features/books/pages/PromotionsPage'));
const SearchResultsPage = lazy(() => import('@/features/books/pages/SearchResultsPage'));
const CategoriesPage = lazy(() => import('@/features/categories/pages/CategoriesPage'));

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
    <Route path="/reset-password" element={<BlankLayout><ResetPasswordPage /></BlankLayout>} />
  </>
);
