import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';

import { adminRoutes } from '@/app/routes/adminRoutes';
import { errorRoutes } from '@/app/routes/errorRoutes';
import { publicRoutes } from '@/app/routes/publicRoutes';
import { userRoutes } from '@/app/routes/userRoutes';

export function RouterProvider() {
  return (
    <BrowserRouter>
      <Routes>
        {publicRoutes}
        {userRoutes}
        {adminRoutes}
        {errorRoutes}
        <Route path="*" element={<Navigate to="/404" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
