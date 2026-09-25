import { Suspense } from 'react';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';

import { adminRoutes } from '@/app/routes/adminRoutes';
import { errorRoutes } from '@/app/routes/errorRoutes';
import { publicRoutes } from '@/app/routes/publicRoutes';
import { userRoutes } from '@/app/routes/userRoutes';

export function RouterProvider() {
  return (
    <BrowserRouter>
      <Suspense fallback={<div className="mx-auto mt-20 h-64 w-full max-w-container-max animate-pulse rounded-xl bg-surface-container" aria-label="Đang tải trang" />}>
        <Routes>
          {publicRoutes}
          {userRoutes}
          {adminRoutes}
          {errorRoutes}
          <Route path="*" element={<Navigate to="/404" replace />} />
        </Routes>
      </Suspense>
    </BrowserRouter>
  );
}
