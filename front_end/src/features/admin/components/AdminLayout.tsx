import type { ReactNode } from 'react';

import AdminHeader from '@/features/admin/components/AdminHeader';
import AdminSidebar from '@/features/admin/components/AdminSidebar';

const AdminLayout = ({ children }: { children: ReactNode }) => (
  <div className="min-h-screen bg-background text-on-surface">
    <AdminSidebar />
    <div className="min-h-screen lg:pl-64">
      <AdminHeader />
      <main className="px-4 py-8 sm:px-6 lg:px-8">{children}</main>
    </div>
  </div>
);

export default AdminLayout;
