import { Route } from 'react-router-dom';

import { MainLayout } from '@/components/layout/Layout';
import ForbiddenPage from '@/features/errors/pages/ForbiddenPage';
import NetworkErrorPage from '@/features/errors/pages/NetworkErrorPage';
import NotFoundPage from '@/features/errors/pages/NotFoundPage';
import ServerErrorPage from '@/features/errors/pages/ServerErrorPage';
import UnauthorizedPage from '@/features/errors/pages/UnauthorizedPage';

export const errorRoutes = (
  <>
    <Route path="/401" element={<MainLayout><UnauthorizedPage /></MainLayout>} />
    <Route path="/403" element={<MainLayout><ForbiddenPage /></MainLayout>} />
    <Route path="/404" element={<MainLayout><NotFoundPage /></MainLayout>} />
    <Route path="/500" element={<MainLayout><ServerErrorPage /></MainLayout>} />
    <Route path="/network-error" element={<MainLayout><NetworkErrorPage /></MainLayout>} />
  </>
);
