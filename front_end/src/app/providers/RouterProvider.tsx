import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';

import { adminRoutes } from '@/app/routes/adminRoutes';
import { publicRoutes } from '@/app/routes/publicRoutes';
import { userRoutes } from '@/app/routes/userRoutes';

export function RouterProvider() {
  return (
    <BrowserRouter>
      <Routes>
        {publicRoutes}
        {userRoutes}
        {adminRoutes}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
